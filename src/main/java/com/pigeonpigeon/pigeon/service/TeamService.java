package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.controller.dto.req.TeamRequestDto;
import com.pigeonpigeon.pigeon.document.*;
import com.pigeonpigeon.pigeon.document.emb.TeamPlayerId;
import com.pigeonpigeon.pigeon.repository.AnswerRepository;
import com.pigeonpigeon.pigeon.repository.GameRepository;
import com.pigeonpigeon.pigeon.repository.TeamPlayerRepository;
import com.pigeonpigeon.pigeon.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {


    private static final int MAX_PLAYERS_PER_TEAM = 5;
    private static final int MAX_TEAMS_PER_GAME = 2;
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;

    private final GameRepository gameRepository;
    private final AnswerRepository answerRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public Team createTeam(TeamRequestDto team) {

        Optional<Game> gameOptional = gameRepository.findByCode(team.getGameCode());

        if (gameOptional.isEmpty()) {
            throw new PigeonException(HttpStatus.NOT_FOUND, "Game not found");
        }

        Game game = gameOptional.get();

        Team newTeam = Team.builder()
                .game(game)
                .name(team.getName())
                .build();

        game.getTeams().add(newTeam);
        gameRepository.save(game);

        return teamRepository.save(newTeam);
    }

    public Team findTeamToJoin(Game game) {
        long totalPlayers = game.getTeams().stream()
                .mapToLong(team -> team.getPlayers().size())
                .sum();

        if (totalPlayers >= MAX_TEAMS_PER_GAME * MAX_PLAYERS_PER_TEAM) {
            throw new PigeonException(HttpStatus.BAD_REQUEST, "Game is full");
        }

        return game.getTeams().stream()
                .min((team1, team2) -> {
                    int size1 = team1.getPlayers().size();
                    int size2 = team2.getPlayers().size();

                    return Integer.compare(size1, size2);
                })
                .filter(team -> team.getPlayers().size() < MAX_PLAYERS_PER_TEAM)
                .orElseThrow(() -> new PigeonException(HttpStatus.BAD_REQUEST, "Cannot find a team to join"));
    }

    public boolean isGameReady(Game game) {
        return game.getTeams().stream()
                .allMatch(team ->
                        !team.getPlayers().isEmpty() && team.getPlayers().size() <= MAX_PLAYERS_PER_TEAM
                );
    }

    public Team addPlayerToTeam(Team team, Player player) {
        if (team.getPlayers().size() >= MAX_PLAYERS_PER_TEAM) {
            throw new PigeonException(HttpStatus.BAD_REQUEST, "Team is full");
        }

        boolean isLeader = team.getPlayers().isEmpty();

        TeamPlayer teamPlayer = TeamPlayer.builder()
                .id(TeamPlayerId.builder().playerId(player.getId()).teamId(team.getId()).build())
                .team(team)
                .isLeader(isLeader)
                .player(player)
                .build();

        team.getPlayers().add(teamPlayer);
        player.getTeams().add(teamPlayer);

        team = teamRepository.save(team);
        teamPlayerRepository.save(teamPlayer);

        return team;

    }

    public Pair<Team, Team> findWinnerAndLoser(Game game) {
        List<Team> teamsWithZeroTokens = game.getTeams().stream()
                .filter(team -> team.getTokens() == 0)
                .toList();

        if (teamsWithZeroTokens.size() != 1) {
            throw new PigeonException(HttpStatus.NOT_FOUND, "None of the teams have lost all their tokens");
        }

        Team loser = teamsWithZeroTokens.get(0);
        Team winner = game.getTeams().stream()
                .filter(team -> !team.equals(loser))
                .findFirst()
                .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "No winner found"));

        return new Pair<>(winner, loser);
    }

    public boolean allTeamsHaveTokensLeft(Game game) {
        return game.getTeams().stream()
                .noneMatch(team -> team.getTokens() == 0);
    }

    public void updateTokens(Round round) {
        List<Answer> answers = answerRepository.findByRound_Id(round.getId());

        for (Answer answer : answers) {
            Team team = answer.getTeam();
            if (!answer.getIsCorrect()) {
                team.decrementTokens();
                teamRepository.save(team);

                messagingTemplate.convertAndSend("/topic/game/" + round.getGame().getCode() + "/token-update",
                        "The team " + team.getName() + " lost a token. Remaining: " + team.getTokens());


            }
        }
    }


}
