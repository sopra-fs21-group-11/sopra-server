package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.repositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.repositoryObjects.Deck;
import ch.uzh.ifi.hase.soprafs21.rest.dto.CardGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.CompareTypeGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.DeckPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CardGetDTO.class)
public interface DeckMapper {
    DeckGetDTO map(Deck deck);
    DeckMapper INSTANCE = Mappers.getMapper(DeckMapper.class);

    @Mapping(source="name", target="name")
    @Mapping(source="description", target="description")
    Deck ConvertDeckPostDTOToEntity(DeckPostDTO deckPostDTO);

    @Mapping(source="id", target="id")
    @Mapping(source="name", target="name")
    @Mapping(source="description", target="description")
    CompareTypeGetDTO ConvertEntityToCompareTypeGetDTO(CompareType type);
}
