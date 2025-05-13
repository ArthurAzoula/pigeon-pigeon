package com.pigeonpigeon.pigeon.document;

import com.pigeonpigeon.pigeon.document.emb.TeamPlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_player")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamPlayer {

    @EmbeddedId
    private TeamPlayerId id;

    private Boolean isLeader = false;

    @ManyToOne
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    private Player player;
}
