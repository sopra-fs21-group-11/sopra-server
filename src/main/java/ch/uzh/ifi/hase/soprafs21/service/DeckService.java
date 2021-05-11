package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.NormalLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.SwissLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.PopulationValueCategory;
import ch.uzh.ifi.hase.soprafs21.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.CompareTypeRepository;
import ch.uzh.ifi.hase.soprafs21.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPutDTO;
import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

@Service
@Transactional
public class DeckService {


    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final CompareTypeRepository compareTypeRepository;

    private FetchingService fetchingService;

    @Autowired
    public DeckService(@Qualifier("deckRepository") DeckRepository deckRepository,
                       @Qualifier("cardRepository") CardRepository cardRepository,
                       @Qualifier("compareTypeRepository") CompareTypeRepository compareTypeRepository, FetchingService fetchingService){
        this.cardRepository = cardRepository;
        this.compareTypeRepository = compareTypeRepository;
        this.deckRepository = deckRepository;
        this.fetchingService = fetchingService;
    }

    //basically a converter that converts the repository object to a Entity objects that we can play with.
    public ch.uzh.ifi.hase.soprafs21.entity.Deck makeDeckReadyToPlay(long id){
        Deck storedDeck = getDeck(id);
        ch.uzh.ifi.hase.soprafs21.entity.Deck playingDeck = new ch.uzh.ifi.hase.soprafs21.entity.Deck();
        //create stack:
        Stack<ch.uzh.ifi.hase.soprafs21.entity.Cards.Card> stack = new Stack<>();
        for(Card card : storedDeck.getCards()){ //add every card to our new created stack.
            stack.add(this.makeCardReadyToPlay(card));
        }
        playingDeck.setCards(stack);
        for(var category : compareTypeRepository.findAll()){
            if(category.getId() == 1){
                playingDeck.addValueCategory(new ECoordinateCategory());
            } else if (category.getId() == 2){
                playingDeck.addValueCategory(new NCoordinateCategory());
            }else if(category.getId() == 3){
                playingDeck.addValueCategory(new PopulationValueCategory());
            }
        }
        return playingDeck;
    }

    //basically a converter that converts the repository object to a Entity objects that we can play with.
    public ch.uzh.ifi.hase.soprafs21.entity.Cards.NormalLocationCard makeCardReadyToPlay(Card card){
        NormalLocationCard locationCard = new NormalLocationCard();
        locationCard.setPopulation(card.getPopulation());
        locationCard.setCardId(card.getId());
        locationCard.setLocationName(card.getName());
        locationCard.setEwCoordinates(card.geteCoordinate());
        locationCard.setNsCoordinates(card.getnCoordinate());
        return  locationCard;

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
    public Deck fetchDeck(long id, String querry, long population){
        Deck deckToFetch = getDeck(id);
        deckToFetch.setCards(new ArrayList<>());//we empty our deck.
        List<Card> cardsToFetch = fetchingService.fetchCardsFromCountry(querry, population, this.getAllCards());
        int cardsAdded = 0;
        for(int i=0;  i<60 && i<cardsToFetch.size() ;i++){
            deckToFetch.addCard(createNewCard(cardsToFetch.get(i)));
            cardsAdded++;
        }
        deckToFetch.setSize(cardsAdded);
        deckToFetch = deckRepository.save(deckToFetch);
        deckRepository.flush();
        validateDeck(deckToFetch.getId());
        return deckToFetch;
    }
    public String fetchingAvailable(){
        String response = fetchingService.fetchingAvailable();
        if(response.contains("slots available now")){
            return "true";
        }else{
            String[] lines = response.split("\n");
            for(String line : lines){
                if(line.startsWith("Slot available after"));
                if(line.length()<=49){
                    continue;
                }
                response = line.substring(47,49);
                return response;
            }
        }
        return response;
    }

    //This is the main initializer for the valueCategories(=CompareTypes):
    //this method gets called only at startup.
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

    //this method initializes our default deck that gets loaded from our .csv
    public void initializeDefaultDeck() {
        Deck defaultDeck = new Deck();
        defaultDeck.setName("Default Deck");
        defaultDeck.setDescription("This is a default deck with swiss location cards like the original game.");
        defaultDeck = createEmptyDeck(defaultDeck);
        List<Card> cardsToSave = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/bunzendataset.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                //skip title row
                if (values[0].equals("Locationname")) {
                    continue;
                }
                Card newCard = new Card();
                newCard.setName(values[0]);
                //split coordinates
                String toFloat = values[1].split(" ")[0] + "." + values[1].split(" ")[1];
                newCard.setnCoordinate(Float.parseFloat(toFloat));
                toFloat = values[2].split(" ")[0] + "." + values[2].split(" ")[1];
                newCard.seteCoordinate(Float.parseFloat(toFloat));
                if (values[3].length() == 0) {//Population
                    continue;
                }
                else {
                    newCard.setPopulation(Integer.parseInt(values[3]));
                }
                cardsToSave.add(newCard);
            }
        }
        catch (Exception ex) {
        }
        List<Card> cardsToAddToDeck = new ArrayList<>();
        //save all cards:
        for(Card cardToSave : cardsToSave){
            cardsToAddToDeck.add(cardRepository.save(cardToSave));
        }
        cardRepository.flush();
        List<Card> definitiveDeck = new ArrayList<>();
        //default deck has 32 cards in it and 3 evaluation happen:
        for(int i = 0;i<=32;i++){
            Random rand = new Random();
            int index = rand.nextInt(32-1);
            while(definitiveDeck.contains(cardsToAddToDeck.get(rand.nextInt(32-1)))){
                index = rand.nextInt(cardsToAddToDeck.size()-1);
            }
            for(var card : definitiveDeck){
                if(cardsToAddToDeck.get(index).getId() == card.getId()){
                    i--;
                    continue;
                }
            }
            definitiveDeck.add(cardsToAddToDeck.get(index));//pick a random card out of the dataset
        }
        //add list to deck:
        defaultDeck.setCards(definitiveDeck);
        defaultDeck.setSize(definitiveDeck.size());
        defaultDeck = deckRepository.save(defaultDeck);
        deckRepository.flush();
        validateDeck(defaultDeck.getId());

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
