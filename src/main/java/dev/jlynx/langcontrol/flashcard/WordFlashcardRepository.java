package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardView;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WordFlashcardRepository extends
        ListCrudRepository<WordFlashcard, Long>,
        ListPagingAndSortingRepository<WordFlashcard, Long>,
        WordFlashcardRepositoryCustom {

    @Query("select w from WordFlashcard w where w.deck.userProfile.id = :id")
    Page<WordFlashcardView> findAllFlashcardViews(@Param("id") Long userProfileId, Pageable pageable);

    Page<WordFlashcardView> findByDeck_Id(Long id, Pageable pageable);

    @Query("select w from WordFlashcard w where w.deck.id = ?1 and w.nextView <= ?2")
    List<WordFlashcardView> findReadyForViewByDeck(Long deckId, LocalDateTime viewsBeforeDatetime, Sort sort, Limit limit);

    @Transactional
    @Modifying
    @Query("""
            update WordFlashcard w set
            w.targetWord = :targetWord,
            w.translatedWord = :translatedWord,
            w.partOfSpeech = :partOfSpeech,
            w.dynamicExamples = :dynamicExamples,
            w.targetExample = :targetExample,
            w.translatedExample = :translatedExample
            where w.id = :id
            """)
    int updateById(@Param("id") Long id,
                   @Param("targetWord") String targetWord,
                   @Param("translatedWord") String translatedWord,
                   @Param("partOfSpeech") PartOfSpeech partOfSpeech,
                   @Param("dynamicExamples") boolean dynamicExamples,
                   @Param("targetExample") String targetExample,
                   @Param("translatedExample") String translatedExample);


}
