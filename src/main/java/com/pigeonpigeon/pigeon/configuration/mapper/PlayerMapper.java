package com.pigeonpigeon.pigeon.configuration.mapper;

import com.pigeonpigeon.pigeon.controller.dto.res.PlayerDto;
import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.document.TeamPlayer;
import com.pigeonpigeon.pigeon.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlayerMapper {

    public static PlayerDto mapPlayer(Player player) {
        return PlayerDto.builder()
                .id(player.getId())
                .email(player.getEmail())
                .pseudonym(player.getPseudonym())
                .createdAt(player.getCreatedAt())
                .build();
    }

    public static PlayerDto mapTeamPlayer(TeamPlayer teamPlayer) {

        Player player = teamPlayer.getPlayer();

        return PlayerDto.builder()
                .id(player.getId())
                .email(player.getEmail())
                .pseudonym(player.getPseudonym())
                .createdAt(player.getCreatedAt())
                .build();
    }
}
