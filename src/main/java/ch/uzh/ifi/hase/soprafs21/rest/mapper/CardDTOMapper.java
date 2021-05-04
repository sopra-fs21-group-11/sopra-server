package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.rest.dto.CardGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.CardPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardDTOMapper {
    CardDTOMapper INSTANCE = Mappers.getMapper(CardDTOMapper.class);

    @Mapping(source="id", target="id")
    @Mapping(source="name", target="name")
    @Mapping(source="nCoordinate", target="nCoordinate")
    @Mapping(source="eCoordinate", target="eCoordinate")
    @Mapping(source="population", target="population")
    CardGetDTO convertEntityToCardGetDTO(Card card);

    @Mapping(source="name", target="name")
    @Mapping(source="nCoordinate", target="nCoordinate")
    @Mapping(source="eCoordinate", target="eCoordinate")
    @Mapping(source="population", target="population")
    Card convertCardPostDTOToCard(CardPostDTO cardPostDTO);
}
