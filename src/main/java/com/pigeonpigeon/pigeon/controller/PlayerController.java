package com.pigeonpigeon.pigeon.controller;

import com.pigeonpigeon.pigeon.configuration.mapper.PlayerMapper;
import com.pigeonpigeon.pigeon.controller.dto.res.PlayerDto;
import com.pigeonpigeon.pigeon.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
@Validated
@ControllerAdvice
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDto>> getPlayers() {
        return ResponseEntity.ok(playerService.getPlayers());
    }
}
