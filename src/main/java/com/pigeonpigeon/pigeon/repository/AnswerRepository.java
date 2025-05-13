package com.pigeonpigeon.pigeon.repository;

import com.pigeonpigeon.pigeon.document.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> { }
