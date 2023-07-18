package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends ListCrudRepository<Flashcard, Long>, FlashcardRepositoryCustom {

    List<Flashcard> findByDeck(Deck deck);
}