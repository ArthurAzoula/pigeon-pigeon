package com.pigeonpigeon.pigeon.configuration.mapper;

import com.pigeonpigeon.pigeon.controller.dto.res.AuthResponseDto;
import com.pigeonpigeon.pigeon.controller.dto.res.PlayerDto;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public static AuthResponseDto mapResponse(PlayerDto player) {
        return AuthResponseDto.builder()
                .message("Authentication successful")
                .player(player)
                .build();
    }
}
