package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.TeamPlayer;
import com.pigeonpigeon.pigeon.document.emb.TeamPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, TeamPlayerId> {

    List<TeamPlayer> findByTeam_Id(UUID teamId);
}
