package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardDTOMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DeckMapper;
import ch.uzh.ifi.hase.soprafs21.service.DeckService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeckController {
    private final DeckService deckService;

    DeckController(DeckService deckService){
        this.deckService = deckService;
    }


    @PutMapping("/decks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeckGetDTO editDeck(@PathVariable long id, @RequestBody DeckPutDTO deckPutDTO){
        Deck deckToEdit = deckService.editDeck(id, deckPutDTO);
        return DeckMapper.INSTANCE.map(deckToEdit);
    }

    @GetMapping("decks/{id}/validate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeckGetDTO validateDeck(@PathVariable long id){
        Deck validatedDeck = deckService.validateDeck(id);
        return DeckMapper.INSTANCE.map(validatedDeck);
    }

    @GetMapping("decks/comparetypes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CompareTypeGetDTO> getCompareTypes(){
        List<CompareTypeGetDTO> typeList = new ArrayList<>();
        for(var type : deckService.getCompareTypes()){
            typeList.add(DeckMapper.INSTANCE.ConvertEntityToCompareTypeGetDTO(type));
        }
        return typeList;
    }




    //The following are the default GET & POST endpoints. These are rather simple...

    @GetMapping("/decks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DeckGetDTO> getAllDecks(){
        List<DeckGetDTO> decks = new ArrayList<>();
        for(Deck deck : deckService.getAllDecks()){
            decks.add(DeckMapper.INSTANCE.map(deck));
        }
        return decks;
    }
    @GetMapping("/decks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeckGetDTO getDeck(@PathVariable long id){
        return DeckMapper.INSTANCE.map(deckService.getDeck(id));
    }
    @PostMapping("/decks")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DeckGetDTO createNewDeck(@RequestBody DeckPostDTO deckPostDTO){
        Deck newDeck = DeckMapper.INSTANCE.ConvertDeckPostDTOToEntity(deckPostDTO);
        newDeck = deckService.createEmptyDeck(newDeck);
        return DeckMapper.INSTANCE.map(newDeck);
    }


    @GetMapping("/cards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CardGetDTO> getAllCards(){
        List<CardGetDTO> cards = new ArrayList<>();
        for(Card card : deckService.getAllCards()){
            cards.add(CardDTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
        }
        return cards;
    }
    @GetMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CardGetDTO getCard(@PathVariable long id){
        return CardDTOMapper.INSTANCE.convertEntityToCardGetDTO(deckService.getCard(id));
    }

    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CardGetDTO createNewCard(@RequestBody CardPostDTO cardPostDTO){
        Card newCard = CardDTOMapper.INSTANCE.convertCardPostDTOToCard(cardPostDTO);
        newCard = deckService.createNewCard(newCard);
        return CardDTOMapper.INSTANCE.convertEntityToCardGetDTO(newCard);
    }

}
