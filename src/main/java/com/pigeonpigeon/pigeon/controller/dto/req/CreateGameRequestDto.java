package com.pigeonpigeon.pigeon.controller.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CreateGameRequestDto {
    private UUID playerGameCreatorId;
}
