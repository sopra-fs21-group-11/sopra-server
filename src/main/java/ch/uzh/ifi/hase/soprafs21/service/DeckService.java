package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.CompareTypeRepository;
import ch.uzh.ifi.hase.soprafs21.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Deck validateDeck(long id){
        Deck deckToValidate = getDeck(id);
        List<CompareType> compareTypes = new ArrayList<>();
        boolean validLong = true;
        boolean validLat = true;
        boolean validPop=true;
        for(Card card : deckToValidate.getCards()){
            if(card.geteCoordinate() == 0){
                validLat = false;
            }
            if(card.getnCoordinate()==0){
                validLong = false;
            }
            if(card.getPopulation()==0){
                validPop = false;
            }
        }
        if(validLat){
            var type = getCompareType(1);//1 is always latitude
            compareTypes.add(type);
        }
        if(validLong){
            var type = getCompareType(2);//2 is always longitude
            compareTypes.add(type);
        }
        if(validPop){
            var type = getCompareType(3);//3 is always population
        }
        if(compareTypes.size()<2){
            //do nothing. we let the validation status false.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deck is invalid. Please change it and revalidate again.");
        }
        deckToValidate.setCompareTypes(compareTypes);
        deckToValidate.setReadyToPlay(true);
        Deck deckToReturn = deckRepository.save(deckToValidate);
        deckRepository.flush();
        return deckToReturn;
    }


    public Deck editDeck(long id, DeckPutDTO deckPutDTO){
        Deck deckToEdit = getDeck(id);
        if(deckPutDTO.getName()!=null){
            deckToEdit.setName(deckPutDTO.getName());
        }
        if(deckPutDTO.getDescription()!=null){
            deckToEdit.setDescription(deckPutDTO.getDescription());
        }
        if(deckPutDTO.getCards().size()!=0){
            List<Card> cardList = new ArrayList<>();
            for(long cardId : deckPutDTO.getCards()){
                Card cardToAdd = getCard(cardId);
                cardList.add(cardToAdd);
            }
            deckToEdit.setCards(cardList);
            deckToEdit.setSize(cardList.size());
        }
        deckToEdit.setReadyToPlay(false);//we changed it so we have to revalidate.
        deckToEdit = deckRepository.save(deckToEdit);
        deckRepository.flush();
        return deckToEdit;
    }



    //This is the main initializer for the valueCategories(=CompareTypes):
    public void initializeValueCategories(){
        CompareType compareType = new CompareType();
        compareType.setId(1L);
        compareType.setName("Longitude Compare Type");
        compareType.setDescription("Compare each card with its longitude.");
        compareTypeRepository.save(compareType);

        compareType = new CompareType();
        compareType.setId(2L);
        compareType.setName("Latitude Compare Type");
        compareType.setDescription("Compare each card with its latitude.");
        compareTypeRepository.save(compareType);

        compareType = new CompareType();
        compareType.setId(3L);
        compareType.setName("Population Compare Type");
        compareType.setDescription("Compare each card with its population.");
        compareTypeRepository.save(compareType);
        compareTypeRepository.flush();
    }

    //these are the getMethods for CompareTypes:
    public CompareType getCompareType(long id){
        Optional<CompareType> optionalType = compareTypeRepository.findById(id);
        if(!optionalType.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CompareType is not in the repository.");
        }
        return optionalType.get();
    }

    public List<CompareType> getCompareTypes(){
        return compareTypeRepository.findAll();
    }


    //These are the rather simple creation and retrieval methods...
    public List<Deck> getAllDecks(){
        return this.deckRepository.findAll();
    }
    public Deck getDeck(long id){
        Optional<Deck> optionalDeck = deckRepository.findById(id);
        if(!optionalDeck.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck is not in repository.");
        }
        return optionalDeck.get();
    }

    public Deck createEmptyDeck(Deck newDeck){
        Deck returningDeck = deckRepository.save(newDeck);
        deckRepository.flush();
        return returningDeck;
    }

    public Card createNewCard(Card card){
        Card returningCard = cardRepository.save(card);
        cardRepository.flush();
        return returningCard;
    }

    public List<Card> getAllCards(){
        return this.cardRepository.findAll();
    }
    public Card getCard(long id){
      Optional<Card> optionalCard = cardRepository.findById(id);
      if(!optionalCard.isPresent()){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card is not in repository.");
      }
      return optionalCard.get();
    }


}
