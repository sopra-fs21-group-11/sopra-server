package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

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
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);
        GameLobby lobby = gameService.createNewGameLobby(testUser);

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testUserPassword");
        User joiningUser = userService.createUser(user);

        gameService.joinGameLobby(joiningUser, lobby.getId());

        Game startedGame = gameService.startGame(lobby);
        assertTrue(gameService.getAllOpenGames().size()==2); //open game is now running. There are still 2 open games because we created 3.
        assertTrue(gameService.getAllRunningGames().size()==1);//running game should be here
        assertEquals(startedGame.getId(), lobby.getId());//id sharing mechanics from lobby -> game.

        Game searchedGame = gameService.getRunningGameById(startedGame.getId());
        assertEquals(searchedGame.getId(), startedGame.getId());

        Game joinedGame = gameService.joinRunningGame(createdUser, "testSessID", startedGame.getId());
        assertTrue(joinedGame.getJoinedPlayer().size()==1);
        joinedGame = gameService.joinRunningGame(joiningUser,"testSessID2", startedGame.getId());
        assertTrue(joinedGame.getPlayers().size()==2);

        joiningUser.setCurrentToken(4);
        createdUser.setCurrentToken(4);

        //new valueCategory for compare mechanics:
        ValueCategory ns = new NCoordinateCategory();
        ValueCategory ew = new ECoordinateCategory();
        //joinedGame.

        TestPlacementAndDoubting(joinedGame, createdUser, joiningUser);


    }

    private void TestPlacementAndDoubting(Game game, User user, User user2){
        long id = game.getId();
        String session = "testSessID";
        Card nextCard = game.getNextCard();
        long startingCardId = game.convertToDTO().getStartingCard().getId();
        float startingCardNCoord = game.convertToDTO().getStartingCard().getNcoord(); //for testing reason, we gonna cheat a little :)
        float startingCardECoord = game.convertToDTO().getStartingCard().getEcoord();
        if(startingCardECoord <= nextCard.getEwCoordinates()){//we place it on the right for a correct placement
            game.performTurn(user.getId(), nextCard,0, "right");
        }else {//we place it on the left for a correct placement
            game.performTurn(user.getId(), nextCard,0, "left");
        }


        int currentToken = user.getCurrentToken();
        //the countdown should be on now. lets doubt:
        gameService.doubtAction(id,(int)nextCard.getCardId(), (int)startingCardId,"testSessID2");
        int tokenAfterDoubt = user.getCurrentToken();
        assertTrue(currentToken+1==tokenAfterDoubt);

        try{Thread.sleep(game.getCurrentSettings().getVisibleAfterDoubtCountdown()*1000+100);} catch (Exception ex){} //wait for the visible cd is finished add 100 for safety reason.

        //do the same vertical and with wrong placement. and with other user
        nextCard = game.getNextCard();
        if(startingCardNCoord >= nextCard.getNsCoordinates()){//we place it on the top for a correct placement
            game.performTurn(user.getId(), nextCard,0, "bottom");
        }else {//we place it on the bottom for a correct placement
            game.performTurn(user.getId(), nextCard,0, "top");
        }

        currentToken = user2.getCurrentToken();
        //the countdown should be on now. lets doubt:
        gameService.doubtAction(id,(int)nextCard.getCardId(), (int)startingCardId,"testSessID");
        tokenAfterDoubt = user2.getCurrentToken();
        assertTrue(currentToken-1==tokenAfterDoubt);
    }

}