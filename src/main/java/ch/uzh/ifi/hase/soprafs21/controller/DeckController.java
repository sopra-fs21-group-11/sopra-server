package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DeckMapper;
import ch.uzh.ifi.hase.soprafs21.service.DeckService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeckController {
    private final DeckService deckService;

    DeckController(DeckService deckService){
        this.deckService = deckService;
    }

    @GetMapping("/decks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DeckGetDTO> getAllDecks(){
        DeckGetDTO dto = new DeckGetDTO();
        deckService.createATestDeck();
        List<DeckGetDTO> decks = new ArrayList<>();
        for(Deck deck : deckService.getAllDecks()){
            decks.add(DeckMapper.INSTANCE.map(deck));
        }
        return decks;
    }
}
