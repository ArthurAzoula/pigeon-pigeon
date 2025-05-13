package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.controller.dto.req.TeamRequestDto;
import com.pigeonpigeon.pigeon.document.Game;
import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.document.Team;
import com.pigeonpigeon.pigeon.document.TeamPlayer;
import com.pigeonpigeon.pigeon.document.emb.TeamPlayerId;
import com.pigeonpigeon.pigeon.repository.GameRepository;
import com.pigeonpigeon.pigeon.repository.TeamPlayerRepository;
import com.pigeonpigeon.pigeon.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private static final int MIN_PLAYERS_PER_TEAM = 1;
    private static final int MAX_PLAYERS_PER_TEAM = 5;
    private static final int MAX_TEAMS_PER_GAME = 2;
    private final TeamRepository teamRepository;
    private final TeamPlayerRepository teamPlayerRepository;

    private final GameRepository gameRepository;


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


}
