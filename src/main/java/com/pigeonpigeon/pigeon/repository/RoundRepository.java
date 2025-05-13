package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoundRepository extends JpaRepository<Round, UUID> {

}
