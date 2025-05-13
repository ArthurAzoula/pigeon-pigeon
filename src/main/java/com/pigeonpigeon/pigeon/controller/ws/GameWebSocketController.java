package com.pigeonpigeon.pigeon.controller.ws;

import com.pigeonpigeon.pigeon.configuration.exception.ErrorDetails;
import com.pigeonpigeon.pigeon.configuration.exception.PigeonException;
import com.pigeonpigeon.pigeon.controller.dto.req.CreateGameRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.req.JoinGameRequestDto;
import com.pigeonpigeon.pigeon.controller.dto.req.StartGameRequest;
import com.pigeonpigeon.pigeon.controller.dto.res.GameResponseDto;
import com.pigeonpigeon.pigeon.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    @MessageMapping("/game/create")
    public void createGame(@Payload CreateGameRequestDto createGameRequest) {
        GameResponseDto gameResponseDto = gameService.createGame(createGameRequest.getPlayerGameCreatorId());
        messagingTemplate.convertAndSend("/topic/game/" + gameResponseDto.getCode(), gameResponseDto);
    }

    @MessageMapping("/game/join/{gameCode}")
    public void joinGame(@DestinationVariable String gameCode, @Payload JoinGameRequestDto joinRequest) {
        try {
            GameResponseDto updatedGame = gameService.joinGame(gameCode, joinRequest.getPlayerId());
            messagingTemplate.convertAndSend("/topic/game/" + gameCode, updatedGame);
        } catch (PigeonException e) {
            handleException(e, gameCode);
        }
    }

    @MessageMapping("/game/start/{gameCode}")
    public void startGame(@DestinationVariable String gameCode, @Payload StartGameRequest startGameRequest){
        try {
            GameResponseDto updatedGame = gameService.start(gameCode, startGameRequest.getPlayerId());
            messagingTemplate.convertAndSend("/topic/game/" + gameCode, updatedGame);
        } catch (PigeonException e) {
            handleException(e, gameCode);
        }
    }

    private void handleException(PigeonException e, String playerId) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), e.getHttpStatus(), e.getMessage());
        messagingTemplate.convertAndSendToUser(
                playerId,
                "/queue/errors",
                errorDetails
        );
    }

}
