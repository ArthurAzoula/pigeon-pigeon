package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.configuration.enums.TeamName;
import com.pigeonpigeon.pigeon.document.Answer;
import com.pigeonpigeon.pigeon.document.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    List<Answer> findByTeam_NameAndRound_Id(TeamName name, UUID roundId);
    List<Answer> findByRound_Id(UUID roundId);
}
