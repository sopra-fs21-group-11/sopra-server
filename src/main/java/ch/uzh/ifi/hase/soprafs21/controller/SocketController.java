package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.*;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    private final UserService userService;
    private final GameService gameService;

    private boolean run = false;
    private SimpMessagingTemplate template;

    @Autowired
    public SocketController(SimpMessagingTemplate template, UserService userService, GameService gameService){
        this.template = template;
        this.userService = userService;
        this.gameService = gameService;
    }

    @MessageMapping("/game")
    public void joinGame(@Header("simpSessionId") String sessionId, JoinGameDTO joinGameDTO) throws Exception {

        User joiningUser = userService.getUser(joinGameDTO.getId());
        Game joinedGame = gameService.joinRunningGame(joiningUser, sessionId,joinGameDTO.getGameId());
        if(gameService.gameIsFull(joinGameDTO.getGameId())){
            gameService.sendGameStateToUsers(joinedGame.getId());
        }
    }

    @MessageMapping("/game/turn")
    public void performTurn(@Header("simpSessionId") String sessionId, GameTurnDTO gameTurnDTO) throws Exception {
        gameService.incomingTurn(gameTurnDTO.getGameId(), sessionId, gameTurnDTO.getPlacementIndex(), gameTurnDTO.getAxis());
        gameService.sendGameStateToUsers(gameTurnDTO.getGameId());
    }

    @MessageMapping("/game/doubt")
    public void doubt(@Header("simpSessionId") String sessionId, DoubtDTO gameDoubtDTO) throws Exception {
        gameService.doubtAction(gameDoubtDTO.getGameId(), gameDoubtDTO.getPlacedCard(), gameDoubtDTO.getDoubtedCard(), sessionId);

    }

    @MessageMapping("/game/guess")
    public void guess(@Header("simpSessionId") String sessionId, GameGuessDTO gameGuessDTO) throws Exception{
        gameService.parseEvaluationGuess(gameGuessDTO.getGameId(), sessionId, gameGuessDTO);
    }

    @MessageMapping("/game/end")
    public void end(@Header("simpSessionId") String sessionId, GameEndDTO gameEndDTO)throws Exception {

        if(gameService.endingAllowed(gameEndDTO.getGameId(), sessionId)) {
            gameService.gameEnded(gameEndDTO.getGameId());
        }
    }



}
