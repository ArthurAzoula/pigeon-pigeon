package com.pigeonpigeon.pigeon.controller.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterPlayerDto {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Pseudonym is required")
    private String pseudonym;

    @NotBlank(message = "Password is required")
    private String password;
}
