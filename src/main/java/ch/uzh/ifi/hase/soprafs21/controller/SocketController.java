package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.JoinGameDTO;
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
        String test = "test";
        User joiningUser = userService.getUser(joinGameDTO.getId());
        gameService.joinRunningGame(joiningUser, sessionId,joinGameDTO.getGameId());

    }

/*
    @MessageMapping("/start")
    public void startCountdown(TestMessage message){
        try{
        } catch (Exception ex){}
        if(message.getStart()=="stop"){

        }
        if(!message.getStart().equals("start")){
            return;
        }
        try {
            this.startCountdown(message.getCd());
        } catch (Exception ex){}

    }


    }*/
}
