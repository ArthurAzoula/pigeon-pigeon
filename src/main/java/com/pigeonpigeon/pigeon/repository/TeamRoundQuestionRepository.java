package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.TeamRoundQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRoundQuestionRepository extends JpaRepository<TeamRoundQuestion, UUID> {

    List<TeamRoundQuestion> findByTeam_Id(UUID teamId);
    Optional<TeamRoundQuestion> findByTeam_IdAndRound_Id(UUID teamId, UUID roundId);
}
