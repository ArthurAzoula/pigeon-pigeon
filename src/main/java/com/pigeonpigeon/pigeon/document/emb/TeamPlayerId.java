package com.pigeonpigeon.pigeon.document.emb;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamPlayerId implements Serializable {

    private UUID teamId;
    private UUID playerId;

}
