package ch.uzh.ifi.hase.soprafs21.repository;



import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("deckRepository")
public interface DeckRepository extends JpaRepository<Deck, Long> {
}



