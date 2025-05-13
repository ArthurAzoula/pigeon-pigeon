package com.pigeonpigeon.pigeon.document;

import com.pigeonpigeon.pigeon.configuration.enums.GameStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "game", uniqueConstraints = {
        @UniqueConstraint(name = "game_code_unique", columnNames = "code")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private Integer currentRound;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Team> teams;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Round> rounds;

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
    }

    public void start() {
        this.status = GameStatus.IN_PROGRESS;
    }

    public void incrementRound() {
        this.currentRound++;
    }
}
