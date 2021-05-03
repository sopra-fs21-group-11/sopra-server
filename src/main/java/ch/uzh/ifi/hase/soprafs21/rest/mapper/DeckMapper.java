package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.rest.dto.CardGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckGetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CardGetDTO.class)
public interface DeckMapper {
    DeckGetDTO map(Deck deck);
    DeckMapper INSTANCE = Mappers.getMapper(DeckMapper.class);

}
