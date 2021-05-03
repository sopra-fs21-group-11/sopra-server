package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.CompareTypeRepository;
import ch.uzh.ifi.hase.soprafs21.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DeckService {


    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final CompareTypeRepository compareTypeRepository;

    @Autowired
    public DeckService(@Qualifier("deckRepository") DeckRepository deckRepository,
                       @Qualifier("cardRepository") CardRepository cardRepository,
                       @Qualifier("compareTypeRepository") CompareTypeRepository compareTypeRepository){
        this.cardRepository = cardRepository;
        this.compareTypeRepository = compareTypeRepository;
        this.deckRepository = deckRepository;
    }

    public List<Deck> getAllDecks(){
        return this.deckRepository.findAll();
    }

    public void createATestDeck(){
        Card newCard = new Card();
        newCard.setName("testCard");
        newCard.seteCoordinate(1.0F);
        newCard.setnCoordinate(2.0F);
        cardRepository.save(newCard);
        cardRepository.flush();
        Deck newDeck = new Deck();
        //newDeck.setCards(new ArrayList<>());
        newDeck.addCard(newCard);
        newDeck.setDescription("This is a testdeck");
        newDeck.setName("TestDeck");
        deckRepository.save(newDeck);
        deckRepository.flush();
    }


}
