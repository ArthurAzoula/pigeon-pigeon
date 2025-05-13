package com.pigeonpigeon.pigeon.controller.dto.req;

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
public class AnswerRequestDto {

    private TeamName team;
    private UUID roundId;
    private List<AnswerPlayerDto> answers;

}
