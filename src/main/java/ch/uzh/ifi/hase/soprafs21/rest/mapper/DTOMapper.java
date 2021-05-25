package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "totalWins", target = "totalWins")
    @Mapping(source = "totalDefeats", target = "totalDefeats")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "currentToken", target = "currentToken")
    @Mapping(source = "totalTokens", target = "totalTokens")
    @Mapping(source = "isPlaying", target = "isPlaying")
    @Mapping(source = "playTime", target = "playTime")
    @Mapping(source = "isOnline", target = "isOnline")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertEntityToUserPutDTO(UserPutDTO userPutDTO);
}

