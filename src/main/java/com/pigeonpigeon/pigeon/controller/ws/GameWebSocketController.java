package com.pigeonpigeon.pigeon.controller.ws;

import com.pigeonpigeon.pigeon.configuration.exception.ErrorDetails;
import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.controller.dto.req.AnswerRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.req.CreateGameRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.req.JoinGameRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.req.VoteRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.res.GameResponseDto;
import com.pigeonpigeon.pigeon.document.*;
import com.pigeonpigeon.pigeon.repository.AnswerRepository;
import com.pigeonpigeon.pigeon.repository.PlayerRepository;
import com.pigeonpigeon.pigeon.repository.VoteRepository;
import com.pigeonpigeon.pigeon.service.AnswerService;
import com.pigeonpigeon.pigeon.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;
    private final AnswerRepository answerRepository;
    private final AnswerService answerService;
    private final PlayerRepository playerRepository;
    private final VoteRepository voteRepository;

    @MessageMapping("/game/create")
    public void createGame(@Payload CreateGameRequestDto createGameRequest) {
        GameResponseDto gameResponseDto = gameService.createGame(createGameRequest.getPlayerGameCreatorId());
        messagingTemplate.convertAndSend("/topic/game/" + gameResponseDto.getCode(), gameResponseDto);
    }

    @MessageMapping("/game/join/{gameCode}")
    public void joinGame(@DestinationVariable String gameCode, @Payload JoinGameRequestDto joinRequest) {
        try {
            GameResponseDto updatedGame = gameService.joinGame(gameCode, joinRequest.getPlayerId());
            messagingTemplate.convertAndSend("/topic/game/" + gameCode, updatedGame);
        } catch (PigeonException e) {
            handleException(e, gameCode);
        }
    }

    @MessageMapping("/game/start/{gameCode}")
    public void startGame(@DestinationVariable String gameCode) {
        try {
            GameResponseDto updatedGame = gameService.start(gameCode);
            messagingTemplate.convertAndSend("/topic/game/" + gameCode, updatedGame);
        } catch (PigeonException e) {
            handleException(e, gameCode);
        }
    }

    @MessageMapping("/game/{gameCode}/answer")
    public void receiveAnswer(@DestinationVariable String gameCode, @Payload AnswerRequestDto dto) {
        Game game = gameService.getGameByCode(gameCode);
        Round currentRound = game.getRounds().stream()
                .filter(round -> round.getId().equals(dto.getRoundId()))
                .findFirst()
                .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Round not found"));

        Team team = game.getTeams().stream()
                .filter(t -> t.getName() == dto.getTeam())
                .findFirst()
                .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Team not found"));

        answerService.processTeamAnswer(currentRound, team, dto.getAnswers());
    }

    @MessageMapping("/game/{gameCode}/vote")
    public void receiveVote(@DestinationVariable String gameCode, @Payload VoteRequestDto dto) {
        Game game = gameService.getGameByCode(gameCode);
        Round currentRound = game.getRounds().stream()
                .filter(round -> round.getId().equals(dto.getRoundId()))
                .findFirst()
                .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Round not found"));

        Answer targetAnswer = answerRepository.findById(dto.getAnswerId())
                .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Answer not found"));

        Player player = playerRepository.findById(dto.getVotePlayerId()).orElseThrow(() ->
                new PigeonException(HttpStatus.NOT_FOUND, "Player not found"));

        Vote vote = Vote.builder()
                .answer(targetAnswer)
                .round(currentRound)
                .player(player)
                .votedAt(Instant.now())
                .build();

        voteRepository.save(vote);

        targetAnswer.getVotes().add(vote);
        answerRepository.save(targetAnswer);
    }

    private void handleException(PigeonException e, String playerId) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), e.getHttpStatus(), e.getMessage());
        messagingTemplate.convertAndSendToUser(
                playerId,
                "/queue/errors",
                errorDetails
        );
    }

}
