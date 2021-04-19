package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.Boolean;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameLobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        given(gameService.createNewGameLobby(any(User.class))).willReturn(lobby);

        MockHttpServletRequestBuilder postRequest = post("/games").contentType(MediaType.APPLICATION_JSON);
        postRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

        given(gameService.getOpenGameById(any(long.class))).willReturn(lobby);
        MockHttpServletRequestBuilder getRequest = get("/games/1").contentType(MediaType.APPLICATION_JSON);
        getRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)lobby.getId())))
                .andExpect(jsonPath("$.hostId", is((int)lobby.getHostId())));

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
        given(gameService.createNewGameLobby(any(User.class))).willReturn(lobby);
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

        Game game = new Game(lobby);
        given(userService.getUserByToken(any(String.class))).willReturn(user);//we need hostuser for comparison check
        given(gameService.getOpenGameById(any(long.class))).willReturn(lobby);
        given(gameService.startGame(any(GameLobby.class))).willReturn(game);

        postRequest = post("/games/1/start").contentType(MediaType.APPLICATION_JSON);
        postRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isOk());


    }

}