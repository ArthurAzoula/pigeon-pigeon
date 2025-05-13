package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.enums.RoundStatus;
import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.controller.dto.req.AnswerPlayerDto;
import com.pigeonpigeon.pigeon.controller.dto.req.AnswerRequestDto;
import com.pigeonpigeon.pigeon.document.Answer;
import com.pigeonpigeon.pigeon.document.Round;
import com.pigeonpigeon.pigeon.document.Team;
import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
import com.pigeonpigeon.pigeon.repository.AnswerRepository;
import com.pigeonpigeon.pigeon.repository.TeamRoundQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private static final long ANSWER_TIME = 120;
    private final AnswerRepository answerRepository;
    private final TeamRoundQuestionRepository teamRoundQuestionRepository;

    public List<Answer> processTeamAnswer(Round round, Team team, List<AnswerPlayerDto> fakeAnswers) {

        if (round.getStatus() != RoundStatus.ON_GOING) {
            throw new PigeonException(HttpStatus.BAD_REQUEST, "Round is not ongoing");
        }

        TeamRoundQuestion teamQuestion = teamRoundQuestionRepository.findByRound_IdAndTeam_Name(round.getId(), team.getName()).orElseThrow(() ->
                new PigeonException(HttpStatus.NOT_FOUND, "Question not found for this round and team"));

        List<Answer> existingAnswers = answerRepository.findByTeam_NameAndRound_Id(team.getName(), round.getId());
        if (!existingAnswers.isEmpty()) {
            throw new PigeonException(HttpStatus.BAD_REQUEST, "Team already answered this round");
        }

        List<Answer> answers = new ArrayList<>();

        for (AnswerPlayerDto answerPlayerDto : fakeAnswers) {
            Answer answer = Answer.builder()
                    .content(answerPlayerDto.getContent())
                    .team(team)
                    .round(round)
                    .votes(new ArrayList<>())
                    .isCorrect(false)
                    .build();
            answers.add(answer);
        }

        return answerRepository.saveAll(answers);
    }

    public boolean waitForAnswers(Round round) {
        final long timeoutMillis = ANSWER_TIME * 1000;
        final long checkInterval = 1000;

        long waited = 0;
        List<Team> teams = round.getGame().getTeams();

        while (waited < timeoutMillis) {

            boolean allTeamsAnswered = true;
            for (Team team : teams) {
                List<Answer> teamAnswers = answerRepository.findByTeam_NameAndRound_Id(team.getName(), round.getId());
                if (teamAnswers.isEmpty()) {
                    allTeamsAnswered = false;
                    break;
                }
            }

            if (allTeamsAnswered) {
                return true;
            }

            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            waited += checkInterval;
        }

        for (Team team : teams) {
            if (answerRepository.findByTeam_NameAndRound_Id(team.getName(), round.getId()).isEmpty()) {
                UUID correctAnswer = round.getQuestions().stream()
                        .filter(q -> q.getTeam().getName().equals(team.getName()))
                        .filter(q -> q.getRound().getId().equals(round.getId()))
                        .map(TeamRoundQuestion::getId)
                        .findFirst()
                        .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Question not found for this round and team"));
                this.processTeamAnswer(round, team, List.of());
            }
        }

        return false;
    }


}
