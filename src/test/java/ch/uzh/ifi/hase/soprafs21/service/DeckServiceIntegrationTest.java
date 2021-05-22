package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.CompareTypeRepository;
import ch.uzh.ifi.hase.soprafs21.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPutDTO;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeckServiceIntegrationTest {
    @Qualifier("deckRepository")
    @Autowired
    private DeckRepository deckRepository;

    @Qualifier("cardRepository")
    @Autowired
    private CardRepository cardRepository;

    @Qualifier("compareTypeRepository")
    @Autowired
    private CompareTypeRepository compareTypeRepository;

    @Autowired
    private DeckService deckService;


    @BeforeAll
    public void testInitialization() throws Exception {
        //default cards & deck &comparetypes should have been initialized already.
        assertTrue(deckService.getAllCards().size()>30);
        assertTrue(deckService.getAllDecks().size()==1);
        assertTrue(deckService.getCompareTypes().size()==3);
        assertTrue(deckService.getAllDecks().get(0).isReadyToPlay());
    }

    @Test
    public void createDeckWithCardsGetValidateAndFetch() throws Exception{

        //Deck creation:
        Deck newDeck = new Deck();
        newDeck.setName("testDeck");
        newDeck.setDescription("TestDescription");
        Deck createdDeck = deckService.createEmptyDeck(newDeck, "default");
        assertNotNull(createdDeck.getId());
        assertEquals(createdDeck.getDescription(), newDeck.getDescription());
        assertEquals(createdDeck.getName(), newDeck.getName());

        //Card creation:
        Card newCard = new Card();
        newCard.setnCoordinate(1.0F);
        newCard.seteCoordinate(2.0F);
        newCard.setName("testCard");
        newCard.setPopulation(3000L);
        Card createdCard = deckService.createNewCard(newCard);
        assertNotNull(createdCard.getId());
        assertEquals(createdCard.getName(), newCard.getName());
        assertEquals(createdCard.getnCoordinate(), newCard.getnCoordinate());
        assertEquals(createdCard.geteCoordinate(), newCard.geteCoordinate());
        assertEquals(createdCard.getPopulation(), newCard.getPopulation());

        //test get methods:
        //cards:
        assertEquals(createdCard.getName(), deckService.getCard(createdCard.getId()).getName());
        //decks:
        assertEquals(createdDeck.getName(), deckService.getDeck(createdDeck.getId()).getName());

        DeckPutDTO deckPutDTO = new DeckPutDTO();
        deckPutDTO.setName("newTestName");
        deckPutDTO.setDescription("newDescription");
        List<Long> cardsList = new ArrayList<>();
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        deckPutDTO.setCards(cardsList);

        //edit deck: //should throw an error since there are not that much cards.
        assertThrows(ResponseStatusException.class, () -> {deckService.editDeck(createdDeck.getId(), deckPutDTO);});
        //add some more :)
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        cardsList.add(createdCard.getId());
        deckPutDTO.setCards(cardsList);

        //validate Deck:
        Deck editedDeck = deckService.editDeck(createdDeck.getId(), deckPutDTO);
        Deck validatedDeck = deckService.validateDeck(editedDeck.getId());
        assertTrue(validatedDeck.isReadyToPlay());
        assertTrue(validatedDeck.getCompareTypes().size()!=0);

        //fetch Deck:
        //fetch deck with switzerland and >= 100'000 population.
        Deck fetchedDeck = deckService.fetchDeck(validatedDeck.getId(), "Italy", 500000);
        //if the population of the swiss cities dont change, the result should stay the same:
        //7 cards with bern on top.
        while(!deckService.fetchingAvailable().equals("true")){//wait for free slot
            Thread.sleep(1000);
        }
        assertTrue(fetchedDeck.getCards().size()==24);
        assertTrue(fetchedDeck.getCards().get(0).getName().equals("Milan"));
        assertTrue(fetchedDeck.isReadyToPlay());

    }

    @Test
    public void makeDeckReadyToPlay() throws Exception{
        Deck deckToMakeReady = null;
        for(var deck : deckService.getAllDecks()){
            if(deck.getName().equals("Default Deck")){
                deckToMakeReady = deck;
            }
        }
        ch.uzh.ifi.hase.soprafs21.entity.Deck playDeck = deckService.makeDeckReadyToPlay(deckToMakeReady.getId());
        assertEquals(playDeck.size(), deckToMakeReady.getSize());
        playDeck.pop();
        assertEquals(playDeck.size()+1, deckToMakeReady.getSize());
    }
}
