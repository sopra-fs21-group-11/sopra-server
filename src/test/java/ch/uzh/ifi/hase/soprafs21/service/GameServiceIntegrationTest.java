package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.SwissLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.Evaluation;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.EvaluatedGameStateDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameGuessDTO;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.GameStateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.beans.PropertyChangeEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void CreateGameLobbyGetByIDAndStart() throws Exception {

        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        GameLobby lobby = gameService.createNewGameLobby(testUser);

        //hostassigning correct?
        assertTrue(lobby.getHostId() == createdUser.getId());
        //lobby added to the gameServiceList?
        assertTrue(gameService.getAllOpenGames().size()>0);

        GameLobby searchedLobby = gameService.getOpenGameById(lobby.getId());
        assertEquals(searchedLobby.getId(), lobby.getId());


    }

    @Test
    public void JoinGame() throws Exception {
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);
        GameLobby lobby = gameService.createNewGameLobby(testUser);

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testUserPassword");
        User joiningUser = userService.createUser(user);

        gameService.joinGameLobby(joiningUser, lobby.getId());
        assertTrue(lobby.getPlayers().size() == 2); //host and joined player

        //TODO: Enable test. This isnt working. -> multiple join by the same user.
        /*assertThrows(Exception.class, () -> {
            gameService.joinGameLobby(joiningUser, lobby.getId());
        });*/
    }

    @Test
    public void KickPlayer() throws Exception {
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);
        GameLobby lobby = gameService.createNewGameLobby(testUser);

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testUserPassword");
        User joiningUser = userService.createUser(user);

        gameService.joinGameLobby(joiningUser, lobby.getId());

        gameService.kickPlayer(createdUser, joiningUser, lobby.getId());
        assertTrue(lobby.getPlayers().size() == 1); //only host ingame because joiner has been kicked

    }

    @Test
    public void StartAndJoinRunningGame() throws Exception {
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("host");

        User createdUser = userService.createUser(testUser);
        GameLobby lobby = gameService.createNewGameLobby(testUser);

        User user = new User();
        user.setUsername("guest");
        user.setPassword("testUserPassword");
        User joiningUser = userService.createUser(user);

        gameService.joinGameLobby(joiningUser, lobby.getId());

        Game startedGame = gameService.startGame(lobby);
        assertTrue(gameService.getAllOpenGames().size()==2); //open game is now running. There are still 2 open games because we created 3.
        assertTrue(gameService.getAllRunningGames().size()==1);//running game should be here
        assertEquals(startedGame.getId(), lobby.getId());//id sharing mechanics from lobby -> game.

        Game searchedGame = gameService.getRunningGameById(startedGame.getId());
        assertEquals(searchedGame.getId(), startedGame.getId());

        Game joinedGame = gameService.joinRunningGame(createdUser, "hostSessID", startedGame.getId());
        //assertTrue(joinedGame.getJoinedPlayer().size()==1);
        joinedGame = gameService.joinRunningGame(joiningUser,"guestSessID", startedGame.getId());
        assertTrue(joinedGame.getPlayers().size()==2);
        //assertTrue(joinedGame.getJoinedPlayer().size()==0);//game is full -> call full game
        assertTrue(gameService.gameIsFull(joinedGame.getId()));//deliver tokens and rearrange queue

        joiningUser.setCurrentToken(4);
        createdUser.setCurrentToken(4);

        TestPlacementAndDoubting(joinedGame, createdUser, joiningUser);
    }

    private void TestPlacementAndDoubting(Game game, User host, User guest){
        long id = game.getId();
        Card nextCard = game.getNextCard();
        long startingCardId = game.convertToDTO().getStartingCard().getId();
        float startingCardNCoord = game.convertToDTO().getStartingCard().getNcoord(); //for testing reason, we gonna cheat a little :)
        float startingCardECoord = game.convertToDTO().getStartingCard().getEcoord();

        if(startingCardECoord <= nextCard.getEwCoordinates()){//we place it on the right for a correct placement
            game.performTurn(host.getId(), nextCard,0, "right");
        }else {//we place it on the left for a correct placement
            game.performTurn(host.getId(), nextCard,0, "left");
        }


        int currentToken = host.getCurrentToken();
        //the countdown should be on now. lets doubt:
        gameService.doubtAction(id,(int)nextCard.getCardId(), (int)startingCardId,"guestSessID");
        int tokenAfterDoubt = host.getCurrentToken();
        assertTrue(currentToken+1==tokenAfterDoubt);

        try{Thread.sleep(game.getCurrentSettings().getVisibleAfterDoubtCountdown()*1000+100);} catch (Exception ex){} //wait for the visible cd is finished add 100 for safety reason.

        //do the same vertical and with wrong placement. and with other host
        nextCard = game.getNextCard();
        if(startingCardNCoord >= nextCard.getNsCoordinates()){
            //we place it on the top for a wrong placement
            game.performTurn(guest.getId(), nextCard,0, "top");
        }
        else {
            //we place it on the bottom for a wrong placement
            game.performTurn(guest.getId(), nextCard,0, "bottom");
        }

        currentToken = guest.getCurrentToken();
        //the countdown should be on now. lets doubt:
        gameService.doubtAction(id,(int)nextCard.getCardId(), (int)startingCardId,"hostSessID");
        tokenAfterDoubt = guest.getCurrentToken();
        assertTrue(currentToken-1==tokenAfterDoubt);

        //lets place some more correct cards:

        //get coordinates of startingcard to place on the correct side of the starting card.
        float ecoord = game.convertToDTO().getStartingCard().getEcoord();
        float ncoord = game.convertToDTO().getStartingCard().getNcoord();
        //We create a mock card because placing and checking would be very difficult


        User user = host;//next turn is host. We have to switch every time, we place a card.

        if(game.convertToDTO().getStartingCard().getRightNeighbour()==0){
            SwissLocationCard cardToPlace = new SwissLocationCard();
            cardToPlace.setEwCoordinates(ecoord+0.1F);
            cardToPlace.setNsCoordinates(ncoord+0.1F);
            cardToPlace.setLocationName("MockCard");
            cardToPlace.setCardId(999L);
            game.performTurn(user.getId(),cardToPlace,0, "right");
            if(user == host){
                user = guest;
            }else{
                user = host;
            }
            try{Thread.sleep(game.getCurrentSettings().getDoubtCountdown()*1000+100);} catch (Exception ex){} //wait for the visible cd is finished add 100 for safety reason.

        }

        if(game.convertToDTO().getStartingCard().getLeftNeighbour()==0){
            SwissLocationCard cardToPlace = new SwissLocationCard();
            cardToPlace.setEwCoordinates(ecoord-0.1F);
            cardToPlace.setNsCoordinates(ncoord-0.1F);
            cardToPlace.setLocationName("MockCard");
            cardToPlace.setCardId(998L);
            game.performTurn(user.getId(),cardToPlace,0, "left");

            if(user == host){
                user = guest;
            }else{
                user = host;
            }
            try{Thread.sleep(game.getCurrentSettings().getDoubtCountdown()*1000+100);} catch (Exception ex){} //wait for the visible cd is finished add 100 for safety reason.

        }

        if(game.convertToDTO().getStartingCard().getHigherNeighbour()==0){
            SwissLocationCard cardToPlace = new SwissLocationCard();
            cardToPlace.setEwCoordinates(ecoord+0.1F);
            cardToPlace.setNsCoordinates(ncoord+0.1F);
            cardToPlace.setLocationName("MockCard");
            cardToPlace.setCardId(997L);
            game.performTurn(user.getId(),cardToPlace,0, "top");

            if(user == host){
                user = guest;
            }else{
                user = host;
            }
            try{Thread.sleep(game.getCurrentSettings().getDoubtCountdown()*1000+100);} catch (Exception ex){} //wait for the visible cd is finished add 100 for safety reason.

        }

        if(game.convertToDTO().getStartingCard().getLowerNeighbour()==0){
            SwissLocationCard cardToPlace = new SwissLocationCard();
            cardToPlace.setEwCoordinates(ecoord-0.1F);
            cardToPlace.setNsCoordinates(ncoord-0.1F);
            cardToPlace.setLocationName("MockCard");
            cardToPlace.setCardId(996L);
            game.performTurn(user.getId(),cardToPlace,0, "bottom");

            if(user == host){
                user = guest;
            }else{
                user = host;
            }
            try{Thread.sleep(game.getCurrentSettings().getDoubtCountdown()*1000+100);} catch (Exception ex){} //wait for the visible cd is finished add 100 for safety reason.

        }

        GameStateDTO stateDTO = game.convertToDTO();
        //now we have a neighbour on every side. Lets evaluate:
        //game.propertyChange(new PropertyChangeEvent());

        //game.propertyChange(new PropertyChangeEvent(null, "DoubtCdEnded"+Long.toString(game.getId()),true, true));


        /*Evaluation evaluation = new Evaluation(game.getPlayers(), game.getCurrentSettings().getTokenGainOnCorrectGuess(), game.getCurrentSettings().getTokenGainOnNearestGuess());
        game.setEvaluation(evaluation);
        String sessId = game.getCurrentPlayer().getValue();
        GameGuessDTO guessDTO = new GameGuessDTO();
        guessDTO.setGameId(game.getId());
        guessDTO.setNrOfWrongPlacedCards("1");
        gameService.parseEvaluationGuess(game.getId(), sessId, guessDTO);
        EvaluatedGameStateDTO evaluatedGameStateDTO =  game.evaluate(); //evaluation Object missing.
        //all have to be correct

        //rewrite this guy :/
        assertTrue(evaluatedGameStateDTO.getTop().get(0).isCorrect());
        assertTrue(evaluatedGameStateDTO.getBottom().get(0).isCorrect());
        assertTrue(evaluatedGameStateDTO.getLeft().get(0).isCorrect());
        assertTrue(evaluatedGameStateDTO.getRight().get(0).isCorrect());*/

        //Todo: Token assertion


        //end game:
        gameService.gameEnded(game.getId());



    }
}