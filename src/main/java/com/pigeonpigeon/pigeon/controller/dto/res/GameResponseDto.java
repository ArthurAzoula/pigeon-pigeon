package com.pigeonpigeon.pigeon.controller.dto.res;

import com.pigeonpigeon.pigeon.configuration.enums.GameStatus;
import com.pigeonpigeon.pigeon.controller.dto.req.TeamRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponseDto {

    private UUID id;
    private GameStatus status;
    private String code;
    private Integer currentRound;

    private List<TeamResponseDto> teams;

}
