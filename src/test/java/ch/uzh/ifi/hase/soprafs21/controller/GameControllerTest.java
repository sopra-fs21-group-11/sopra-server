package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.entity.cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.cards.NormalLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.valueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.valueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.service.DeckService;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //We still need the userService for authentication handling.
    @MockBean
    private UserService userService;

    @MockBean
    GameService gameService;
    @MockBean
    DeckService deckService;

    //example token...
    private static String authToken;

    @BeforeAll
    public static void prepareToken() { //create new jwt token for authorization
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("soprafs21")
                .setSubject("username")
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        authToken = token;
    }

    @Test
    public void getGameAfterCreateGame() throws Exception {
        // given
        User user = new User();
        user.setId(2L);
        user.setUsername("HostUser");
        user.setIsOnline(false);

        GameLobby lobby = new GameLobby(user);
        lobby.setId(1L);

        given(userService.getUserByToken(any(String.class))).willReturn(user);
        given(gameService.createNewGameLobby(any(User.class), any(GameSettings.class))).willReturn(lobby);

        MockHttpServletRequestBuilder postRequest = post("/games").contentType(MediaType.APPLICATION_JSON);
        postRequest.header("Authorization", "Bearer "+authToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "");
        postRequest.content(jsonObject.toString());
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

        given(gameService.openGameExists(any(Long.class))).willReturn(true);
        given(gameService.getOpenGameById(any(long.class))).willReturn(lobby);
        MockHttpServletRequestBuilder getRequest = get("/games/1").contentType(MediaType.APPLICATION_JSON);
        getRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)lobby.getId())));
                //.andExpect(jsonPath("$.hostId.id", is((int)lobby.getHostId())));
    }

    @Test
    public void StartGameAndJoinGame() throws Exception{
        User user = new User();
        user.setId(2L);
        user.setUsername("HostUser");
        user.setIsOnline(false);

        GameLobby lobby = new GameLobby(user);
        lobby.setId(1L);

        User joiningUser = new User();
        joiningUser.setId(3L);
        joiningUser.setPassword("blabla");
        joiningUser.setUsername("username2");

        given(userService.getUserByToken(any(String.class))).willReturn(joiningUser);
        given(gameService.createNewGameLobby(any(User.class), any(GameSettings.class))).willReturn(lobby);
        lobby.addPlayer(joiningUser);

        //There is only one game so we dont need to match arguments...
        given(gameService.joinGameLobby(any(User.class), any(long.class))).willReturn(lobby);

        MockHttpServletRequestBuilder postRequest = post("/games/1").contentType(MediaType.APPLICATION_JSON);
        postRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)lobby.getId())))
                .andExpect(jsonPath("$.hostId", is((int)lobby.getHostId())))
        .andExpect((jsonPath("$.players[0]", is(user.getId().intValue()))))
        .andExpect((jsonPath("$.players[1]", is(joiningUser.getId().intValue()))));

        //kick request
        lobby.removePlayer(joiningUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/kick").contentType(MediaType.APPLICATION_JSON);
        putRequest.header("Authorization", "Bearer "+authToken);

        //Initialize JSON Object:
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("kickPlayerId", joiningUser.getId());
        putRequest.content(jsonObject.toString());

        given(userService.getUserByToken(any(String.class))).willReturn(user);
        given(userService.getUser(any(long.class))).willReturn(joiningUser);
        given(gameService.kickPlayer(any(User.class), any(User.class), any(long.class))).willReturn(lobby);


        mockMvc.perform(putRequest)
                .andExpect(jsonPath("$.players", hasSize(1)));

        //join again
        lobby.addPlayer(joiningUser);
        //create Deck
        Deck deck = new Deck();
        Card card1 = new NormalLocationCard();
        Card card2 = new NormalLocationCard();
        Stack<Card> stack = new Stack<>();
        stack.add(card1);
        stack.add(card2);
        deck.setCards(stack);;
        deck.addValueCategory(new NCoordinateCategory());
        deck.addValueCategory(new ECoordinateCategory());
        Game game = new Game(lobby, deck);
        given(userService.getUserByToken(any(String.class))).willReturn(user);//we need hostuser for comparison check
        given(gameService.getOpenGameById(any(long.class))).willReturn(lobby);
        given(gameService.startGame(any(GameLobby.class))).willReturn(game);

        postRequest = post("/games/1/start").contentType(MediaType.APPLICATION_JSON);
        postRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isOk());

        //Game should now be started
        given(gameService.getOpenGameById(any(long.class))).willReturn(null); //Opengames has to return null

        given(gameService.getRunningGameById(any(long.class))).willReturn(game);
        MockHttpServletRequestBuilder getRequest = get("/games/1").contentType(MediaType.APPLICATION_JSON);
        getRequest.header("Authorization", "Bearer "+authToken);
        mockMvc.perform(getRequest)
                .andExpect((jsonPath("$.gameStarted", is(true))));
    }
}