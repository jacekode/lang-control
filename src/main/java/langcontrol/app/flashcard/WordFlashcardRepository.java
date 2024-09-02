package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordFlashcardRepository extends ListCrudRepository<WordFlashcard, Long>, WordFlashcardRepositoryCustom {

    List<WordFlashcard> findByDeck(Deck deck);
}