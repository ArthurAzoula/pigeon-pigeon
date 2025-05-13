package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.configuration.enums.GameStatus;
import com.pigeonpigeon.pigeon.document.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

    Optional<Game> findByCode(String code);
    Boolean existsByCode(String code);
    List<Game> findByStatus(GameStatus status);
}
