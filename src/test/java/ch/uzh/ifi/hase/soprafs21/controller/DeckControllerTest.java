package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.rest.dto.CardPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.DeckService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.collection.internal.PersistentList;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(DeckController.class)
public class DeckControllerTest {
    @Autowired
    MockMvc mockMvc;

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
    public void createDeckAndGetDeck() throws Exception {

        Deck newDeck = new Deck();
        newDeck.setName("TestDeck");
        newDeck.setDescription("TestDescription");

        //Initialize JSON Object:
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "TestDeck");
        jsonObject.put("description", "TestDescription");

        given(deckService.createEmptyDeck(any(Deck.class), any(long.class))).willReturn(newDeck);
        MockHttpServletRequestBuilder postRequest = post("/decks").contentType(MediaType.APPLICATION_JSON);
        postRequest.content(jsonObject.toString());
        postRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(newDeck.getName())))
                .andExpect(jsonPath("$.description", is(newDeck.getDescription())));

        newDeck.setId(1L);
        given(deckService.getDeck(any(Long.class))).willReturn(newDeck);
        MockHttpServletRequestBuilder getRequest = get("/decks/1");
        getRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readyToPlay",is(false)))
                .andExpect(jsonPath("$.description", is(newDeck.getDescription())));


        List<Deck> deckList = new ArrayList<>();
        deckList.add(newDeck);
        given(deckService.getAllDecks()).willReturn(deckList);

        getRequest = get("/decks");
        getRequest.header("Authorization", "Bearer "+authToken);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].readyToPlay",is(false)))
                .andExpect(jsonPath("$[0].description", is(newDeck.getDescription())));


        Card cardToAdd = new Card();
        cardToAdd.setName("testCard");
        cardToAdd.setPopulation(100L);
        cardToAdd.seteCoordinate(1.00F);
        cardToAdd.setnCoordinate(2.00F);

        //Initialize JSON Object:
        jsonObject = new JSONObject();
        jsonObject.put("name", "testCard");
        jsonObject.put("nCoordinate", 2.00);
        jsonObject.put("eCoordinate", 1.00);
        jsonObject.put("population", 100L);

        given(deckService.createNewCard(any(Card.class))).willReturn(cardToAdd);
        postRequest = post("/cards").contentType(MediaType.APPLICATION_JSON);
        postRequest.header("Authorization", "Bearer "+authToken);
        postRequest.content(jsonObject.toString());

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(cardToAdd.getName())))
                .andExpect(jsonPath("$.nCoordinate", equalTo((double)cardToAdd.getnCoordinate())))
                .andExpect(jsonPath("$.eCoordinate", equalTo((double)cardToAdd.geteCoordinate())));

        given(deckService.getCard(any(Long.class))).willReturn(cardToAdd);
        getRequest = get("/cards/1");
        postRequest.header("Authorization", "Bearer "+authToken);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cardToAdd.getName())))
                .andExpect(jsonPath("$.nCoordinate", equalTo((double)cardToAdd.getnCoordinate())))
                .andExpect(jsonPath("$.eCoordinate", equalTo((double)cardToAdd.geteCoordinate())));

        List<Card> cardList = new ArrayList<>();
        cardList.add(cardToAdd);
        given(deckService.getAllCards()).willReturn(cardList);
        getRequest = get("/cards");
        getRequest.header("Authorization", "Bearer "+authToken);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(cardToAdd.getName())))
                .andExpect(jsonPath("$[0].nCoordinate", equalTo((double)cardToAdd.getnCoordinate())))
                .andExpect(jsonPath("$[0].eCoordinate", equalTo((double)cardToAdd.geteCoordinate())));
    }

    @Test
    public void editDeck() throws Exception {
        Deck newDeck = new Deck();
        newDeck.setName("TestDeck");
        newDeck.setDescription("TestDescription");
        newDeck.setId(1L);

        Card cardToAdd = new Card();
        cardToAdd.setName("testCard");
        cardToAdd.setPopulation(100L);
        cardToAdd.seteCoordinate(1.00F);
        cardToAdd.setnCoordinate(2.00F);
        cardToAdd.setId(1L);

        newDeck.addCard(cardToAdd);
        newDeck.addCard(cardToAdd);

        List<Card> cardList = new ArrayList<>();
        cardList.add(cardToAdd);
        cardList.add(cardToAdd);
        newDeck.setCards(cardList);
        newDeck.setSize(2);

        given(deckService.editDeck(any(long.class), any(DeckPutDTO.class))).willReturn(newDeck);
        MockHttpServletRequestBuilder putRequest = put("/decks/1").contentType(MediaType.APPLICATION_JSON);
        putRequest.content("{\"cards\":[1, 1],\"name\":\"TestDeck\",\"description\":\"testDescription\"}");
        putRequest.header("Authorization", "Bearer "+authToken);

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newDeck.getName())))
                .andExpect(jsonPath("$.description", is(newDeck.getDescription())))
                .andExpect(jsonPath("$.cards[0].name", is("testCard")))
                .andExpect(jsonPath("$.cards[1].name", is("testCard")));
    }
    @Test
    public void getCompareTypes() throws Exception {

        List<CompareType> types = new ArrayList<>();
        CompareType type = new CompareType();
        type.setId(1L);
        type.setName("Longitude Compare Type");
        type.setDescription("Compare each card with its longitude.");
        types.add(type);
        given(deckService.getCompareTypes()).willReturn(types);

        MockHttpServletRequestBuilder getRequest = get("/decks/comparetypes");
        getRequest.header("Authorization", "Bearer "+authToken);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Longitude Compare Type")));
    }
}
