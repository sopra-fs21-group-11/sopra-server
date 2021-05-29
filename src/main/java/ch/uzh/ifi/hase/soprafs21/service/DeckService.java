package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.Application;
import ch.uzh.ifi.hase.soprafs21.entity.cards.NormalLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.repositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.entity.repositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.repositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.entity.valueCategories.ECoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.entity.valueCategories.NCoordinateCategory;
import ch.uzh.ifi.hase.soprafs21.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.CompareTypeRepository;
import ch.uzh.ifi.hase.soprafs21.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPutDTO;
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
    private final UserService userService;
    private FetchingService fetchingService;

    @Autowired
    public DeckService(@Qualifier("deckRepository") DeckRepository deckRepository,
                       @Qualifier("cardRepository") CardRepository cardRepository,
                       @Qualifier("compareTypeRepository") CompareTypeRepository compareTypeRepository,
                       FetchingService fetchingService,
                       UserService userService){
        this.cardRepository = cardRepository;
        this.compareTypeRepository = compareTypeRepository;
        this.deckRepository = deckRepository;
        this.fetchingService = fetchingService;
        this.userService = userService;
    }

    //basically a converter that converts the repository object to a Entity objects that we can play with.
    public ch.uzh.ifi.hase.soprafs21.entity.Deck makeDeckReadyToPlay(long id){
        Deck storedDeck = getDeck(id);
        ch.uzh.ifi.hase.soprafs21.entity.Deck playingDeck = new ch.uzh.ifi.hase.soprafs21.entity.Deck();
        //create stack:
        Stack<ch.uzh.ifi.hase.soprafs21.entity.cards.Card> stack = new Stack<>();
        for(Card card : storedDeck.getCards()){ //add every card to our new created stack.
            stack.add(this.makeCardReadyToPlay(card));
        }
        playingDeck.setCards(stack);
        for(var category : compareTypeRepository.findAll()){
            if(category.getId() == 1){
                playingDeck.addValueCategory(new ECoordinateCategory());
            } else if (category.getId() == 2){
                playingDeck.addValueCategory(new NCoordinateCategory());
            }
        }
        return playingDeck;
    }

    //basically a converter that converts the repository object to a Entity objects that we can play with.
    public ch.uzh.ifi.hase.soprafs21.entity.cards.NormalLocationCard makeCardReadyToPlay(Card card){
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
        //check if enough cards are in the deck.
        if(deckPutDTO.getCards().size()<10 || deckPutDTO.getCards().size()>60){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are either too less or too many cards in the deck to save it. Please send a correct deck.");
        }
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

    public Deck fetchDeck(long id, String querry, long population, String token){
        User fetchingUser = userService.getUserByToken(token);
        Deck deckToFetch = getDeck(id);
        if(token.equals("0")){
            deckToFetch.setCreatedBy(0L);
        }else {
            deckToFetch.setCreatedBy(fetchingUser.getId());
        }
        deckToFetch.setCards(new ArrayList<>());//we empty our deck.
        List<Card> cardsToFetch = fetchingService.fetchCardsFromCountry(querry, population, this.getAllCards());
        if(cardsToFetch.size()<10){
            try{
                deckRepository.deleteById(deckToFetch.getId());
            }catch (Exception ex){
            }
            deckToFetch = deckRepository.save(deckToFetch);
            deckRepository.flush();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cards were created but the deck couldn't be stored since there are less than 10 cards.");
        }
        int cardsAdded = 0;
        List<Card> allCards = getAllCards();
        for(int i=0;  i<60 && i<cardsToFetch.size() ;i++){
            Card doCreateCard = cardsToFetch.get(i);
            boolean added = false;
            for(var card : allCards){
                if(doCreateCard.getName().equals(card.getName())){
                    cardsAdded++;
                    deckToFetch.addCard(card);
                    added = true;
                    break;
                }
            }
            if(!added) {
                deckToFetch.addCard(createNewCard(cardsToFetch.get(i)));
                cardsAdded++;
            }
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
        }
        else{
            String[] lines = response.split("\n");
            for(String line : lines){
                if(line.length()<=49){
                    continue;
                }
                if(line.startsWith("Slot available after"));{
                response = line.substring(47,49).trim();
                return response;
                }
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
        try {
            Deck defaultDeck = new Deck();
            defaultDeck.setName("Default Deck");
            defaultDeck.setDescription("This is a default deck with swiss location cards like the original game.");
            defaultDeck = createEmptyDeck(defaultDeck, "default");
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
            for (Card cardToSave : cardsToSave) {
                cardsToAddToDeck.add(cardRepository.save(cardToSave));
            }
            cardRepository.flush();
            if(cardsToAddToDeck.size()<=32){
                return;
            }
            List<Card> definitiveDeck = new ArrayList<>();
            //default deck has 32 cards in it and 3 evaluation happen:
            for (int i = 0; i <= 32; i++) {
                definitiveDeck.add(cardsToAddToDeck.get(i));
            }
            //add list to deck:
            defaultDeck.setCards(definitiveDeck);
            defaultDeck.setSize(definitiveDeck.size());
            defaultDeck = deckRepository.save(defaultDeck);
            deckRepository.flush();
            validateDeck(defaultDeck.getId());
        } catch (Exception ex){
            Application.logger.error("************************************");
            Application.logger.error(ex.toString());
        }
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

    public Deck createEmptyDeck(Deck newDeck, String token){
        if(token != "default"){
            newDeck.setCreatedBy(userService.getUserByToken(token).getId());

        }
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

    public List<Card> getCardsNotInDeck(long id){
        Deck deckToReturnCards = getDeck(id);
        List<Card> allCards = getAllCards();
        for(Card card : deckToReturnCards.getCards()){
            for(var cardToRemove  : allCards){
                if(card.getId() == cardToRemove.getId()){
                    allCards.remove(cardToRemove);
                    break;
                }
            }
        }
        return allCards;
    }

    public void cleanupEmptyDecks(){
        List<Deck> allDecks = getAllDecks();
        for(Deck deck : allDecks){
            if(deck.getCards().size()<10){
                deckRepository.deleteById(deck.getId());
            }
        }
        deckRepository.flush();
    }

    public void remove(long id, String token) {

        Deck deckToDelete = getDeck(id);
        if(deckToDelete.getCreatedBy()!=userService.getUserByToken(token).getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete a deck that you haven't created.");
        }try {
            deckRepository.deleteById(id);
            deckRepository.flush();
        } catch (Exception ex){
            Application.logger.error(ex.getMessage());
        }
    }
}
