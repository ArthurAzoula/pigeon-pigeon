package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.enums.RoundStatus;
import com.pigeonpigeon.pigeon.document.Game;
import com.pigeonpigeon.pigeon.document.Round;
import com.pigeonpigeon.pigeon.document.Team;
import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
import com.pigeonpigeon.pigeon.repository.GameRepository;
import com.pigeonpigeon.pigeon.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;
    private final TeamRoundQuestionService teamRoundQuestionService;
    private final GameRepository gameRepository;

    public Round createRound(Game game) {

        Round round = Round.builder()
                .status(RoundStatus.ON_GOING)
                .game(game)
                .build();

        Round savedRound = roundRepository.save(round);

        List<TeamRoundQuestion> questions = createQuestionForTeams(game, savedRound);

        savedRound.setQuestions(questions);

        game.getRounds().add(savedRound);

        gameRepository.save(game);

        return roundRepository.save(savedRound);
    }

    public void setAnswerStep(Round round) {
        round.setStatus(RoundStatus.WAITING_FOR_ANSWERS);
        roundRepository.save(round);
    }

    public void setVotesStep(Round round) {
        round.setStatus(RoundStatus.WAITING_FOR_VOTES);
        roundRepository.save(round);
    }

    public void setEndStep(Round round) {
        round.setStatus(RoundStatus.COMPLETED);
        roundRepository.save(round);
    }

    private List<TeamRoundQuestion> createQuestionForTeams(Game game, Round actualRound) {
        List<Team> teams = game.getTeams();

        return teams.stream()
                .map(team -> teamRoundQuestionService.createQuestion(actualRound, team))
                .toList();
    }

}
