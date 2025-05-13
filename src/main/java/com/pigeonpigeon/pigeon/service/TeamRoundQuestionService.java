package com.pigeonpigeon.pigeon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pigeonpigeon.pigeon.document.Answer;
import com.pigeonpigeon.pigeon.document.Round;
import com.pigeonpigeon.pigeon.document.Team;
import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
import com.pigeonpigeon.pigeon.repository.AnswerRepository;
import com.pigeonpigeon.pigeon.repository.TeamRoundQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamRoundQuestionService {

    private final TeamRoundQuestionRepository teamRoundQuestionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AnswerRepository answerRepository;
    private List<Pair<String, String>> questionsCache;

    public TeamRoundQuestion createQuestion(Round round, Team team) {

        if (questionsCache == null) {
            loadQuestions();
        }

        Pair<String, String> questionAndAnswer = getUniqueQuestionForRound(round);

        TeamRoundQuestion teamRoundQuestion = TeamRoundQuestion.builder()
                .team(team)
                .round(round)
                .question(questionAndAnswer.a)
                .correctAnswer(questionAndAnswer.b)
                .build();

        // create default answer with isCorrect = true
        Answer answer = Answer.builder()
                .content(questionAndAnswer.b)
                .team(team)
                .round(round)
                .votes(new ArrayList<>())
                .isCorrect(true)
                .build();

        answerRepository.save(answer);

        return teamRoundQuestionRepository.save(teamRoundQuestion);
    }

    private Pair<String, String> getUniqueQuestionForRound(Round round) {

        List<TeamRoundQuestion> existingQuestions = teamRoundQuestionRepository.findByRound_Id(round.getId());
        Set<String> questionsAlreadyUsedInRound = new HashSet<>();

        for (TeamRoundQuestion question : existingQuestions) {
            questionsAlreadyUsedInRound.add(question.getQuestion());
        }


        int maxAttempts = 50;
        int attempts = 0;

        while (attempts < maxAttempts) {
            int randomIndex = new Random().nextInt(questionsCache.size());
            Pair<String, String> candidate = questionsCache.get(randomIndex);

            if (!questionsAlreadyUsedInRound.contains(candidate.a)) {
                return candidate;
            }

            attempts++;
        }

        return questionsCache.get(new Random().nextInt(questionsCache.size()));
    }

    private void loadQuestions() {
        try {
            Resource resource = new ClassPathResource("data/questions.json");
            try (InputStream inputStream = resource.getInputStream()) {
                String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                JsonNode jsonNode = objectMapper.readTree(jsonContent);
                ArrayNode questionsArray = (ArrayNode) jsonNode.get("questions");

                questionsCache = new ArrayList<>(questionsArray.size());

                for (JsonNode questionNode : questionsArray) {
                    String question = questionNode.get("question").asText();
                    String correctAnswer = questionNode.get("correct_answer").asText();
                    questionsCache.add(new Pair<>(question, correctAnswer));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load questions from JSON file", e);
        }
    }
}