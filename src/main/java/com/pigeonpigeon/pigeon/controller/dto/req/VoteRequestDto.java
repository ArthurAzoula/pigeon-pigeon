package com.pigeonpigeon.pigeon.controller.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteRequestDto {

    private UUID votePlayerId;
    private UUID roundId;
    private UUID answerId;
}
