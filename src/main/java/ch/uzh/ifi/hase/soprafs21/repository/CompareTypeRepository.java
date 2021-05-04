package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.CompareType;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompareTypeRepository extends JpaRepository<CompareType, Long> {
}
