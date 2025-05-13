package com.pigeonpigeon.pigeon.controller.dto.res;

import com.pigeonpigeon.pigeon.configuration.enums.RoundStatus;
import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
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
public class RoundDto {

    private UUID id;
    private RoundStatus status;
    private GameResponseDto game;
    private List<TeamRoundQuestionDto> question;
}
