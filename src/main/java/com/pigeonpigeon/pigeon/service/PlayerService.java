package com.pigeonpigeon.pigeon.service;

import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.configuration.mapper.PlayerMapper;
import com.pigeonpigeon.pigeon.controller.dto.req.RegisterPlayerDto;
import com.pigeonpigeon.pigeon.controller.dto.res.PlayerDto;
import com.pigeonpigeon.pigeon.document.Player;
import com.pigeonpigeon.pigeon.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService implements UserDetailsService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public List<PlayerDto> getPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(PlayerMapper::mapPlayer)
                .toList();
    }

    public Player getPlayerByEmail(String email) {
        return playerRepository.findByEmail(email)
                .orElseThrow(() -> new PigeonException(HttpStatus.NOT_FOUND, "Player not found"));
    }

    public PlayerDto save(RegisterPlayerDto registerPlayerDto) {
        if (playerRepository.findByEmail(registerPlayerDto.getEmail()).isPresent()) {
            throw new PigeonException(HttpStatus.CONFLICT, "Player already exists");
        }

        Player player = Player.builder()
                .email(registerPlayerDto.getEmail())
                .pseudonym(registerPlayerDto.getPseudonym())
                .password(this.passwordEncoder.encode(registerPlayerDto.getPassword()))
                .createdAt(Instant.now())
                .build();

        player = playerRepository.save(player);
        return PlayerMapper.mapPlayer(player);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Player not found"));
    }

}
