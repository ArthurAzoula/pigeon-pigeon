package com.pigeonpigeon.pigeon.controller.dto.res;

import com.pigeonpigeon.pigeon.configuration.enums.TeamName;
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
public class TeamResponseDto {

    private UUID id;
    private TeamName name;
    private Integer tokens;
    private List<PlayerDto> players;
}
