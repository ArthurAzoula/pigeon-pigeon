package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.configuration.enums.TeamName;
import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRoundQuestionRepository extends JpaRepository<TeamRoundQuestion, UUID> {

    List<TeamRoundQuestion> findByRound_Id(UUID roundId);

    Optional<TeamRoundQuestion> findByRound_IdAndTeam_Name(UUID roundId, TeamName teamName);

}