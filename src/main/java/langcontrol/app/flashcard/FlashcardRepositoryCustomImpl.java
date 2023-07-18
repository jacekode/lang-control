package langcontrol.app.flashcard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import langcontrol.app.deck.Deck;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FlashcardRepositoryCustomImpl implements FlashcardRepositoryCustom {

    private EntityManager em;

    public FlashcardRepositoryCustomImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public List<Flashcard> findReadyForReviewFlashcardsByDeck(Deck deck, LocalDateTime nextLearnViewInUTCBefore,
                                                              LocalDate nextReviewDateLocalBefore, int limit) {
        TypedQuery<Flashcard> query = em.createQuery("SELECT f FROM Flashcard f " +
                "WHERE f.deck = :deck " +
                "AND (f.nextLearnViewInUTC <= :learnViewBefore " +
                "OR f.nextReviewWithoutTimeInUTC <= :reviewBefore)", Flashcard.class);
        query.setParameter("deck", deck);
        query.setParameter("learnViewBefore", nextLearnViewInUTCBefore);
        query.setParameter("reviewBefore", nextReviewDateLocalBefore);
        query.setMaxResults(limit);
        return query.getResultList();
    }

}
