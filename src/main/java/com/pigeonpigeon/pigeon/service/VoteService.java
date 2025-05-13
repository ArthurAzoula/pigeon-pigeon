package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.document.Answer;
import com.pigeonpigeon.pigeon.document.Round;
import com.pigeonpigeon.pigeon.repository.AnswerRepository;
import com.pigeonpigeon.pigeon.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final AnswerRepository answerRepository;

    private static final long VOTE_TIME = 60;

    public boolean waitForVotes(Round round) {
        final long timeoutMillis = VOTE_TIME * 1000;
        final long checkInterval = 1000;

        long waited = 0;

        List<Answer> answers = answerRepository.findByRound_Id(round.getId());

        while (waited < timeoutMillis) {
            boolean allVoted = answers.stream()
                    .allMatch(answer -> answer.getVotes().size() >= round.getGame().getTeams().size() - 1);

            if (allVoted) return true;

            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            waited += checkInterval;
        }
        return false;
    }
}
