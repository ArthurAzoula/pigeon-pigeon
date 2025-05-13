package com.pigeonpigeon.pigeon.controller;

import com.pigeonpigeon.pigeon.configuration.mapper.AuthMapper;
import com.pigeonpigeon.pigeon.configuration.mapper.PlayerMapper;
import com.pigeonpigeon.pigeon.controller.dto.req.AuthRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.req.RegisterPlayerDto;
import com.pigeonpigeon.pigeon.controller.dto.res.AuthResponseDto;
import com.pigeonpigeon.pigeon.controller.dto.res.LogoutResponseDto;
import com.pigeonpigeon.pigeon.controller.dto.res.PlayerDto;
import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.service.PlayerService;
import com.pigeonpigeon.pigeon.utils.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Validated
@ControllerAdvice
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PlayerService playerService;
    private final JwtToken jwtToken;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Validated AuthRequestDto authLogin) throws DisabledException, LockedException, BadCredentialsException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authLogin.getEmail(), authLogin.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Player player = (Player) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtToken.generateResponseCookie(player);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(AuthMapper.mapResponse(PlayerMapper.mapPlayer(player)));

    }

    @PostMapping("/register")
    public ResponseEntity<PlayerDto> register(@RequestBody @Validated RegisterPlayerDto registerPlayerDto) {
        PlayerDto player = playerService.save(registerPlayerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout() {
        SecurityContextHolder.clearContext();

        ResponseCookie responseCookie = jwtToken.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(LogoutResponseDto.builder().message("Logout successful").build());
    }
}
