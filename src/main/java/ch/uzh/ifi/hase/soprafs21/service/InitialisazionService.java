package ch.uzh.ifi.hase.soprafs21.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitialisazionService implements InitializingBean {
    private DeckService deckService;

    @Autowired
    public InitialisazionService(DeckService deckService){
        this.deckService = deckService;
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        deckService.initializeValueCategories();
    }
}
