package dev.jlynx.langcontrol.flashcard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import dev.jlynx.langcontrol.deck.Deck;
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

    // todo: add the ORDER BY mechanism
    @Transactional
    @Override
    public List<WordFlashcard> findAllReadyForViewByDeck(
            Deck deck,
            LocalDateTime viewsBeforeDatetime
    ) {
        TypedQuery<WordFlashcard> query = em.createQuery(
                "SELECT f FROM WordFlashcard f WHERE f.deck = :deck AND f.nextView <= :timestamp",
                WordFlashcard.class
        );
        query.setParameter("deck", deck);
        query.setParameter("timestamp", viewsBeforeDatetime);
        return query.getResultList();
    }
}
