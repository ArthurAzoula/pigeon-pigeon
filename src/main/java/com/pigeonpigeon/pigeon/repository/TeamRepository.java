package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    List<Team> findByGame_Code(String gameCode);

    Optional<Team> findByIdAndGame_Code(UUID id, String gameCode);
}
