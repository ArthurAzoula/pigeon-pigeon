package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    List<Vote> findByPlayer_Id(UUID playerId);
    Optional<Vote> findByPlayer_IdAndAnswer_Id(UUID playerId, UUID answerId);
}
