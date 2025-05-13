package com.pigeonpigeon.pigeon.configuration.mapper;

import com.pigeonpigeon.pigeon.controller.dto.res.TeamResponseDto;
import com.pigeonpigeon.pigeon.document.Team;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TeamMapper {

    public static TeamResponseDto mapToTeamDto(Team team) {
        return TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .tokens(team.getTokens())
                .players(team.getPlayers().stream()
                        .map(PlayerMapper::mapTeamPlayer)
                        .collect(Collectors.toList()))
                .build();
    }
}
