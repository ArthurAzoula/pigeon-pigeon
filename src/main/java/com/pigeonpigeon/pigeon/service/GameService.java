package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.enums.GameStatus;
import com.pigeonpigeon.pigeon.configuration.enums.TeamName;
import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.configuration.mapper.GameMapper;
import com.pigeonpigeon.pigeon.configuration.mapper.RoundMapper;
import com.pigeonpigeon.pigeon.controller.dto.req.TeamRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.res.GameResponseDto;
import com.pigeonpigeon.pigeon.document.Game;
import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.document.Round;
import com.pigeonpigeon.pigeon.document.Team;
import com.pigeonpigeon.pigeon.repository.AnswerRepository;
import com.pigeonpigeon.pigeon.repository.GameRepository;
import com.pigeonpigeon.pigeon.repository.PlayerRepository;
import com.pigeonpigeon.pigeon.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class GameService {

    private final TeamService teamService;
    private final RoundService roundService;
    private final AnswerService answerService;
    private final VoteService voteService;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GameResponseDto createGame(UUID creatorId) {

        Player creator = playerRepository.findById(creatorId).orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Player not found"));

        String code;
        do {
            code = RandomUtils.generateGameCode();
        } while (gameRepository.existsByCode(code));

        Game game = Game.builder()
                .code(code)
                .status(GameStatus.WAITING)
                .currentRound(1)
                .build();

        gameRepository.save(game);

        Team a = teamService.createTeam(
                TeamRequestDto.builder()
                        .gameCode(game.getCode())
                        .name(TeamName.TEAM_BLUE)
                        .build());

        Team b = teamService.createTeam(TeamRequestDto.builder()
                .gameCode(game.getCode())
                .name(TeamName.TEAM_RED)
                .build());

        teamService.addPlayerToTeam(a, creator);

        Game updatedGame = this.getGameByCode(code);

        return GameMapper.mapToGameResponseDto(updatedGame);
    }

    public GameResponseDto joinGame(String gameCode, UUID playerId) {
        Game game = this.getGameByCode(gameCode);

        if (game.getStatus() != GameStatus.WAITING) {
            throw new PigeonException(HttpStatus.CONFLICT, "Game is already started or finished");
        }

        Player player = playerRepository.findById(playerId).orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Player not found"));

        boolean playerAlreadyInGame = game.getTeams().stream()
                .flatMap(team -> team.getPlayers().stream())
                .anyMatch(tp -> tp.getPlayer().getId().equals(playerId));

        if (playerAlreadyInGame) {
            throw new PigeonException(HttpStatus.CONFLICT, "Player already in game");
        }

        Team teamToJoin = teamService.findTeamToJoin(game);

        teamService.addPlayerToTeam(teamToJoin, player);

        Game gameUpdated = gameRepository.findByCode(gameCode).orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Game not found"));

        return GameMapper.mapToGameResponseDto(gameUpdated);
    }

    public GameResponseDto start(String gameCode) {
        Game game = this.getGameByCode(gameCode);

        if (game.getStatus() != GameStatus.WAITING) {
            throw new PigeonException(HttpStatus.CONFLICT, "Game is already started or finished");
        }

        if (!teamService.isGameReady(game)) {
            throw new PigeonException(HttpStatus.CONFLICT, "Game is not ready to start");
        }

        game.start();
        gameRepository.save(game);

        this.runGameAsync(game.getCode());

        return GameMapper.mapToGameResponseDto(game);
    }

    @Async
    public CompletableFuture<Void> runGameAsync(String gameCode) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.run(gameCode);
            } catch (Exception e) {
                messagingTemplate.convertAndSend("/topic/game/" + gameCode + "/error", e.getMessage());
            }
        });
    }

    private void run(String gameCode) {
        Game game = this.getGameByCode(gameCode);

        while (teamService.allTeamsHaveTokensLeft(game)) {

            Round actualRound = roundService.createRound(game);
            game.incrementRound();
            gameRepository.save(game);

            messagingTemplate.convertAndSend("/topic/game/" + gameCode + "/round", RoundMapper.mapToRoundResponseDto(actualRound));

            roundService.setAnswerStep(actualRound);

            boolean answerCollected = answerService.waitForAnswers(actualRound);

            if (!answerCollected) {
                messagingTemplate.convertAndSend("/topic/game/" + gameCode + "/info", "Time's up! None of the teams answered.");
            }

            roundService.setVotesStep(actualRound);

            boolean votesReceived = voteService.waitForVotes(actualRound);

            if (!votesReceived) {
                messagingTemplate.convertAndSend("/topic/game/" + gameCode + "/info", "Time's up! None of the teams voted.");
            }

            roundService.setEndStep(actualRound);
            // notify teams with tokens lost or gained
            teamService.updateTokens(actualRound);
            messagingTemplate.convertAndSend("/topic/game/" + gameCode + "/info", "Round " + game.getCurrentRound() + " finished!");
        }

        // notify teams with game finished
        messagingTemplate.convertAndSend("/topic/game/" + gameCode + "/info", "Game finished!");
    }

    public Game getGameByCode(String gameCode) {
        return gameRepository.findByCode(gameCode).orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Game not found"));
    }
}
