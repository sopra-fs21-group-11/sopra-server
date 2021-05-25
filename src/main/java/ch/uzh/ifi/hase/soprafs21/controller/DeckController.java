package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.CardDTOMapper;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DeckMapper;
import ch.uzh.ifi.hase.soprafs21.service.DeckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeckController {
    private final DeckService deckService;

    DeckController( DeckService deckService)
    {
        this.deckService = deckService;
    }

    @GetMapping("/decks/{id}/fetch")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeckGetDTO fetchDeck(@PathVariable long id, @RequestParam String querry, @RequestParam long population){
        Deck fetchedDeck = deckService.fetchDeck(id, querry, population);
        return DeckMapper.INSTANCE.map(fetchedDeck);
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

    @GetMapping("/decks/{id}/fetch/available")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity fetchingAvailable(){
        String response = deckService.fetchingAvailable();
        return ResponseEntity.status(200).body(response);
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
    public DeckGetDTO getCardsNotInDeck(@PathVariable long id){
        return DeckMapper.INSTANCE.map(deckService.getDeck(id));
    }

    @GetMapping("/decks/{id}/cards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CardGetDTO> getDeck(@PathVariable long id){
        List<CardGetDTO> returnList = new ArrayList<>();
        for(var card : deckService.getCardsNotInDeck(id)){
            returnList.add(CardDTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
        }
        return returnList;
    }

    @GetMapping("/decks/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity deleteDeck(@PathVariable long id, @RequestHeader("Authorization") String token){
        deckService.remove(id, token);
        return ResponseEntity.status(204).build();
    }

    @PostMapping("/decks")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DeckGetDTO createNewDeck(@RequestHeader("Authorization") String token, @RequestBody DeckPostDTO deckPostDTO){
        Deck newDeck = DeckMapper.INSTANCE.ConvertDeckPostDTOToEntity(deckPostDTO);
        newDeck = deckService.createEmptyDeck(newDeck,token);
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
