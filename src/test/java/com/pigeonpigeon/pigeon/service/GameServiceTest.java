package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.enums.GameStatus;
import com.pigeonpigeon.pigeon.configuration.enums.TeamName;
import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.controller.dto.req.TeamRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.res.GameResponseDto;
import com.pigeonpigeon.pigeon.document.Game;
import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.document.Team;
import com.pigeonpigeon.pigeon.document.TeamPlayer;
import com.pigeonpigeon.pigeon.repository.GameRepository;
import com.pigeonpigeon.pigeon.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    private final UUID playerId = UUID.randomUUID();
    private final String gameCode = "XD14";
    @Mock
    private GameRepository gameRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private PlayerRepository playerRepository;
    @InjectMocks
    private GameService gameService;
    private Player player;
    private Game game;
    private Team teamBlue;
    private Team teamRed;

    @BeforeEach
    void setUp() {
        player = Player.builder().id(playerId).pseudonym("pseudonym").build();

        teamBlue = Team.builder().name(TeamName.TEAM_BLUE).players(new ArrayList<>()).build();
        teamRed = Team.builder().name(TeamName.TEAM_RED).players(new ArrayList<>()).build();

        game = Game.builder()
                .code(gameCode)
                .status(GameStatus.WAITING)
                .currentRound(1)
                .teams(new ArrayList<>())
                .build();

        game.getTeams().add(teamBlue);
        game.getTeams().add(teamRed);
    }

    @Test
    void createGameSuccessfully() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.existsByCode(anyString())).thenReturn(false);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(teamService.createTeam(any(TeamRequestDto.class))).thenReturn(teamBlue, teamRed);
        when(gameRepository.findByCode(anyString())).thenReturn(Optional.of(game));

        GameResponseDto result = gameService.createGame(playerId);

        assertNotNull(result);
        verify(teamService).addPlayerToTeam(teamBlue, player);
        verify(gameRepository, times(1)).save(any(Game.class));
        assertEquals(GameStatus.WAITING, game.getStatus());
    }

    @Test
    void createGameWithPlayerNotFound() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(PigeonException.class, () -> gameService.createGame(playerId));

        verify(gameRepository, never()).save(any(Game.class));
        verify(teamService, never()).createTeam(any(TeamRequestDto.class));
    }

    @Test
    void joinGameSuccessfully() {
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.of(game));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamService.findTeamToJoin(game)).thenReturn(teamRed);

        GameResponseDto result = gameService.joinGame(gameCode, playerId);

        assertNotNull(result);
        verify(teamService).addPlayerToTeam(teamRed, player);
    }

    @Test
    void joinGameNotFound() {
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.empty());

        assertThrows(PigeonException.class, () -> gameService.joinGame(gameCode, playerId));

        verify(playerRepository, never()).findById(any());
        verify(teamService, never()).findTeamToJoin(any());
    }

    @Test
    void joinGamePlayerAlreadyInGame() {
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.of(game));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        TeamPlayer teamPlayer = TeamPlayer.builder()
                .player(player)
                .team(teamBlue)
                .build();

        teamBlue.getPlayers().add(teamPlayer);

        assertThrows(PigeonException.class, () -> gameService.joinGame(gameCode, playerId));

        verify(teamService, never()).findTeamToJoin(any());
    }

    @Test
    void startGameSuccessfully() {
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.of(game));
        when(teamService.isGameReady(game)).thenReturn(true);

        GameResponseDto result = gameService.start(gameCode);

        assertNotNull(result);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        verify(gameRepository).save(game);
    }

    @Test
    void startGameNotFound() {
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.empty());

        assertThrows(PigeonException.class, () -> gameService.start(gameCode));

        verify(teamService, never()).isGameReady(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    void startGameAlreadyStarted() {
        game.setStatus(GameStatus.IN_PROGRESS);
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.of(game));

        assertThrows(PigeonException.class, () -> gameService.start(gameCode));

        verify(teamService, never()).isGameReady(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    void startGameNotReady() {
        when(gameRepository.findByCode(gameCode)).thenReturn(Optional.of(game));
        when(teamService.isGameReady(game)).thenReturn(false);

        assertThrows(PigeonException.class, () -> gameService.start(gameCode));

        assertEquals(GameStatus.WAITING, game.getStatus());
        verify(gameRepository, never()).save(any());
    }



}
