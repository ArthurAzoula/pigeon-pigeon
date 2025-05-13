package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.document.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByEmail(String email);
}
