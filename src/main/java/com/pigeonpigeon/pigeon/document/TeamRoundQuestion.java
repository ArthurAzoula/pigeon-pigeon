package com.pigeonpigeon.pigeon.document;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "team_round_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoundQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String question;

    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
