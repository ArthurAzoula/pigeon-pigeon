package com.pigeonpigeon.pigeon.configuration.mapper;

import com.pigeonpigeon.pigeon.controller.dto.res.TeamRoundQuestionDto;
import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
import org.springframework.stereotype.Component;

@Component
public class TeamRoundQuestionMapper {

    public static TeamRoundQuestionDto mapTeamRoundQuestionToDto(TeamRoundQuestion teamRoundQuestion) {
        return TeamRoundQuestionDto.builder()
                .id(teamRoundQuestion.getId())
                .question(teamRoundQuestion.getQuestion())
                .correctAnswer(teamRoundQuestion.getCorrectAnswer())
                .team(TeamMapper.mapToTeamDto(teamRoundQuestion.getTeam()))
                .build();
    }
}
