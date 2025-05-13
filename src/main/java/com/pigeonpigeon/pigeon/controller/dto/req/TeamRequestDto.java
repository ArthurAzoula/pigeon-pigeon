package com.pigeonpigeon.pigeon.controller.dto.req;

import com.pigeonpigeon.pigeon.configuration.enums.TeamName;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRequestDto {



    @NotBlank(message = "Game code is needed")
    private String gameCode;

    private TeamName name;

}
