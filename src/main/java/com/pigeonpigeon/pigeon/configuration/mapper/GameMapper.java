package com.pigeonpigeon.pigeon.configuration.mapper;

import com.pigeonpigeon.pigeon.controller.dto.res.GameResponseDto;
import com.pigeonpigeon.pigeon.document.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public static GameResponseDto mapToGameResponseDto(Game game) {
        return GameResponseDto.builder()
                .id(game.getId())
                .code(game.getCode())
                .status(game.getStatus())
                .currentRound(game.getCurrentRound())
                .teams(game.getTeams().stream()
                        .map(TeamMapper::mapToTeamDto)
                        .toList())
                .build();
    }
}
