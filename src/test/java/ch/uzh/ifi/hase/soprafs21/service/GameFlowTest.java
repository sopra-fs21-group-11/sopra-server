package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameGuessDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

//@WebAppConfiguration
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class GameFlowTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("deckRepository")
    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private DeckService deckService;

    @Autowired
    private GameService gameService;

    private long hostId = 83L;
    private long guestId = 84L;
    private long deckId = 1L;

    private long lobbyId = 1L;

    @Test
    @Order(1)
    public void setup(){
        userRepository.deleteAll();
        //create two users that play together
        User host = new User();
        host.setUsername("host");
        host.setPassword("password");
        hostId = userService.createUser(host).getId();

        User guest = new User();
        guest.setUsername("guest");
        guest.setPassword("password");
        guestId = userService.createUser(guest).getId();


        for(var deck : deckService.getAllDecks()){
            if(deck.getName().equals("Default Deck")){
                deckId = deck.getId();
            }
        }
    }

    @Test
    @Order(2)
    public void CreateAndJoinGameLobby() throws Exception {
        //get users:
        User host = userService.getUser(hostId);
        User guest = userService.getUser(guestId);

        GameLobby lobby = gameService.createNewGameLobby(host);
        //hostassigning correct?
        assertTrue(lobby.getHostId() == host.getId());
        //lobby added to the gameServiceList?
        assertTrue(gameService.getAllOpenGames().size()>0);

        //is lobby in opengames?
        GameLobby searchedLobby = gameService.getOpenGameById(lobby.getId());
        assertEquals(searchedLobby.getId(), lobby.getId());

        //join game
        gameService.joinGameLobby(guest, lobby.getId());
        assertTrue(lobby.getPlayers().size() == 2); //host and joined player

        //assert multiple joins:
        assertThrows(Exception.class, () -> {
            gameService.joinGameLobby(guest, lobby.getId());
        });
        lobbyId = lobby.getId();
    }

    @Test
    @Order(3)
    public void KickPlayerFromGameLobby() throws Exception {
        GameLobby lobby = gameService.getOpenGameById(lobbyId);
        User guest = userService.getUser(guestId);
        User host = userService.getUser(hostId);

        //guest cannot kick a player.
        assertThrows(Exception.class, () -> {
            gameService.kickPlayer(guest, host, 1L);
        });

        lobby = gameService.kickPlayer(host, guest, lobby.getId());
        assertTrue(lobby.getPlayers().size() == 1); //only host ingame because joiner has been kicked

        //join again
        lobby = gameService.joinGameLobby(guest, lobbyId);
        assertTrue(lobby.getPlayers().size() == 2);

        //setup our testSettings:
        GameSettings settings = new GameSettings();
        settings.setPlayerTurnCountdown(5);
        settings.setDoubtCountdown(5);
        settings.setVisibleAfterDoubtCountdown(5);
        settings.setEvaluationCountdownVisible(5);
        settings.setEvaluationCountdown(5);



        lobby.setSettings(settings);

    }

    @Test
    @Order(4)
    public void StartGameAndJoinSocket() throws Exception {
        //get users again:
        User guest = userService.getUser(guestId);
        User host = userService.getUser(hostId);


        //start game
        Game startedGame = gameService.startGame(gameService.getOpenGameById(lobbyId));
        assertTrue(gameService.getAllOpenGames().size()==0); //open game is now running. There are still 2 open games because we created 3.
        assertTrue(gameService.getAllRunningGames().size()==1);//running game should be here
        assertEquals(startedGame.getId(), lobbyId);//id sharing mechanics from lobby -> game.

        //get test
        Game searchedGame = gameService.getRunningGameById(startedGame.getId());
        assertEquals(searchedGame.getId(), startedGame.getId());

        gameService.joinRunningGame(host, "hostSessID", startedGame.getId());

        for(var player : searchedGame.getPlayers()){
            if(player.getKey().getId()==hostId){
                assertEquals(player.getValue(), "hostSessID");//assert hostSessionId is set.
            }else{
                assertEquals(player.getValue(), "");//all other sessIds have to be empty.
            }
        }
        gameService.joinRunningGame(guest,"guestSessID", startedGame.getId());
        for(var player : searchedGame.getPlayers()){
            if(player.getKey().getId()==hostId){
                assertEquals(player.getValue(), "hostSessID");//assert hostSessionId is set.
            }else{
                assertEquals(player.getValue(), "guestSessID");//all other sessIds have to be empty.
            }
        }
        assertTrue(gameService.gameIsFull(searchedGame.getId()));//deliver tokens and rearrange queue
    }

    @Test
    @Order(5)
    public void TurnDoubtEvaluation() throws Exception {
        Game game = gameService.getRunningGameById(lobbyId);
        long startingCardId = game.convertToDTO().getStartingCard().getId();
        long placedCardId = game.getNextCard().getCardId();
        game.performTurn(game.getCurrentPlayer().getKey().getId(), game.getNextCard(), 0, "right");
        Thread.sleep(1000);
        gameService.doubtAction(lobbyId, (int)placedCardId, (int)startingCardId, "guestSessID");
        int[] intArray = new int[2];
        int i = 0;
        for(var player : game.getPlayers()){
            intArray[i] = player.getKey().getCurrentToken();
            i++;
        }
        assertNotEquals(intArray[0], intArray[1]); //a token sharing must have happened.
        Thread.sleep(6000);//wait for the visibleCd.
        String[] axis = new String[4]; //we place the card on all axis randomly
        axis[0]="top";
        axis[1]="bottom";
        axis[2]="left";
        axis[3]="right";
        boolean left=false, top=false, right= false, bottom = false;
        int j = 0;
        int placementIndex = 0;
        while(!game.convertToDTO().getGamestate().equals("EVALUATION")){//place cards until evaluation starts
            placementIndex=0;
            switch (axis[j]) {
                case ("top"):
                    if(!top) {
                        placementIndex = game.convertToDTO().getTop().size();
                        if (placementIndex > 1) {
                            top = true;
                        }
                    }
                    break;
                case("bottom"):
                    if(!bottom) {
                        placementIndex = game.convertToDTO().getBottom().size();
                        if(placementIndex>1){
                            bottom=true;
                        }
                    }
                    break;
                case("left"):
                    if(!left) {
                        placementIndex = game.convertToDTO().getLeft().size();
                        if(placementIndex>1){
                            left=true;
                        }
                    }
                    break;
                case("right"):
                    if(!right) {
                        placementIndex = game.convertToDTO().getRight().size();
                        if(placementIndex>1){
                            right=true;
                        }
                    }
                    break;
            }
            game.performTurn(game.getCurrentPlayer().getKey().getId(), game.getNextCard(), placementIndex, axis[j]);
            Thread.sleep(1000);
            if(j==3){
                j=0;
            }else{
                j++;
            }

        }
        assertEquals(game.convertToDTO().getGamestate(), "EVALUATION");
        Thread.sleep(100);
        GameGuessDTO dto = new GameGuessDTO();
        int hostToken = 0;
        for(var player : game.getPlayers()){
            if(player.getKey().getId() == hostId){
                hostToken = player.getKey().getCurrentToken();
            }
        }
        dto.setNrOfWrongPlacedCards("4");
        dto.setGameId(lobbyId);
        gameService.parseEvaluationGuess(lobbyId, "hostSessID", dto);
        dto.setNrOfWrongPlacedCards("100");//4 must be nearest or correct.
        gameService.parseEvaluationGuess(lobbyId, "guestSessID", dto);
        Thread.sleep(1000); //wait a little for parsing
        int currentHostToken = 0;
        for(var player : game.getPlayers()){
            if(player.getKey().getId() == hostId){
                currentHostToken = player.getKey().getCurrentToken();
            }
        }
        assertTrue(hostToken +1 == currentHostToken || hostToken+2 == currentHostToken);
        assertEquals(game.convertToDTO().getGamestate(), "EVALUATIONVISIBLE");
        Thread.sleep(6000);

        gameService.gameEnded(lobbyId);
        assertEquals(game.convertToDTO().getGamestate(), "GAMEEND");
        assertTrue(gameService.getAllRunningGames().size()==0);
    }

}
