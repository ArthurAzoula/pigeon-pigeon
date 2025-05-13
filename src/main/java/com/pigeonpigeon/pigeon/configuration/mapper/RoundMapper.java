package com.pigeonpigeon.pigeon.configuration.mapper;

import com.pigeonpigeon.pigeon.controller.dto.res.RoundDto;
import com.pigeonpigeon.pigeon.controller.dto.res.TeamRoundQuestionDto;
import com.pigeonpigeon.pigeon.document.Round;
import org.springframework.stereotype.Component;

@Component
public class RoundMapper {

    public static RoundDto mapToRoundResponseDto(Round round) {
        return RoundDto.builder()
                .id(round.getId())
                .status(round.getStatus())
                .game(GameMapper.mapToGameResponseDto(round.getGame()))
                .question(round
                        .getQuestions()
                        .stream()
                        .map(TeamRoundQuestionMapper::mapTeamRoundQuestionToDto)
                        .toList())
                .build();
    }
}
