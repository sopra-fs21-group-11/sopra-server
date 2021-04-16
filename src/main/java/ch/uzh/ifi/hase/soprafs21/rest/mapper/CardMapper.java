package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.rest.socketDTO.CardDTO;

public class CardMapper {
    public static CardDTO ConvertEntityToCardDTO(Card card){
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getCardId());
        cardDTO.setLowerNeighbour(((card.getLowerNeighbour() != null) ? card.getLowerNeighbour().getCardId() : 0));
        cardDTO.setHigherNeighbour(((card.getHigherNeighbour() != null) ? card.getHigherNeighbour().getCardId() : 0));
        cardDTO.setLeftNeighbour(((card.getLeftNeighbour() != null) ? card.getLeftNeighbour().getCardId() : 0));
        cardDTO.setRightNeighbour(((card.getRightNeighbour() != null) ? card.getRightNeighbour().getCardId() : 0));
        cardDTO.setNcoord(card.getNsCoordinates());
        cardDTO.setEcoord(card.getEwCoordinates());
        cardDTO.setName(card.getLocationName());

        //TODO: Needs Population/Area/Height Conversion

        return cardDTO;

    }


}
