package langcontrol.app.flashcard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import langcontrol.app.deck.Deck;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The WordFlashcardRepositoryCustomImpl class provides a JPA implementation of the WordFlashcardRepositoryCustom interface.
 */
public class WordFlashcardRepositoryCustomImpl implements WordFlashcardRepositoryCustom {

    private final EntityManager em;

    /**
     * Constructs a custom WordFlashcard repository with the specified JPA EntityManager
     * @param em A JPA's EntityManager implementation object.
     */
    public WordFlashcardRepositoryCustomImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public List<WordFlashcard> findReadyForReviewFlashcardsByDeck(Deck deck, LocalDateTime viewsBeforeTimestamp, int limit) {
        TypedQuery<WordFlashcard> query = em.createQuery("SELECT f FROM Flashcard f " +
                "WHERE f.deck = :deck " +
                "AND f.nextView <= :timestamp", WordFlashcard.class);
        query.setParameter("deck", deck);
        query.setParameter("timestamp", viewsBeforeTimestamp);
        query.setMaxResults(limit);
        return query.getResultList();
    }

}
