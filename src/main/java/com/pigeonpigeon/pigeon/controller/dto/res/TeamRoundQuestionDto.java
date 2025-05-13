package com.pigeonpigeon.pigeon.controller.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRoundQuestionDto {

    private UUID id;
    private String question;
    private String correctAnswer;
    private TeamResponseDto team;
}
