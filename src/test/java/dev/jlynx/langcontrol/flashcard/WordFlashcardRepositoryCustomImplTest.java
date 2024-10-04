package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.deck.DeckRepository;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.spacedrepetition.LearnModeStep;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class WordFlashcardRepositoryCustomImplTest {

    @Autowired
    private WordFlashcardRepositoryCustomImpl underTest;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private WordFlashcardRepository wordFlashcardRepository;

    @BeforeEach
    void setUp() {
        Deck deck = new Deck();
        deck.setId(null);
        deck.setUserProfile(null);
        deck.setName("test deck");
        deck.setSourceLang(LanguageCode.ENGLISH);
        deck.setTargetLang(LanguageCode.GERMAN);

        WordFlashcard learnCard = new WordFlashcard();
        learnCard.setId(null);
        learnCard.setDeck(deck);
        learnCard.setSourceLang(LanguageCode.ENGLISH);
        learnCard.setTargetLang(LanguageCode.GERMAN);
        learnCard.setTranslatedWord("learn card's front");
        learnCard.setTargetWord("learn card's back");
        learnCard.setCreatedAt(LocalDateTime.of(2017, 5, 21, 16, 37));
        learnCard.setInLearnMode(true);
        learnCard.setLearnModeStep(LearnModeStep.TWO);
        learnCard.setNextView(LocalDateTime.of(2023, 2, 9, 10, 24, 56));
        learnCard.setiFactor(1.3);
        learnCard.setrFactor(0.7);

        WordFlashcard reviewCard = new WordFlashcard();
        reviewCard.setId(null);
        reviewCard.setDeck(deck);
        reviewCard.setSourceLang(LanguageCode.ENGLISH);
        reviewCard.setTargetLang(LanguageCode.GERMAN);
        reviewCard.setTranslatedWord("review card's front");
        reviewCard.setTargetWord("review card's back");
        reviewCard.setCreatedAt(LocalDateTime.of(2017, 5, 21, 16, 37));
        reviewCard.setInLearnMode(false);
        reviewCard.setLearnModeStep(null);
        reviewCard.setNextView(null);
        reviewCard.setNextReviewInUTC(LocalDateTime.of(2023, 3, 16, 0, 0, 0, 0));
        reviewCard.setNextReviewWithoutTimeInUTC(reviewCard.getNextReviewInUTC().toLocalDate());
        reviewCard.setiFactor(1.3);
        reviewCard.setrFactor(0.7);

        deck.setFlashcards(List.of(learnCard, reviewCard));
        deckRepository.save(deck);
    }

    @AfterEach
    void tearDown() {
        deckRepository.deleteAll();
        wordFlashcardRepository.deleteAll();
    }

    @Test
    public void findAllReadyForViewCards_WhenDateTimesAreTooEarly() {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDateTime nextLearnViewInUTCBefore = LocalDateTime.of(2023, 2, 7, 8, 13);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();
        int limit = 10;

        // when
        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(0, readyForReviewFlashcards.size());
    }

    @ParameterizedTest
    @MethodSource("dateTimesInTheMiddle")
    public void findAllReadyForReviewFlashcardsByDeck_ShouldReturnOnlyFlashcardsScheduledForEarlierOrEqualToParam(
            LocalDateTime nextLearnViewInUTCBefore) {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();
        int limit = 10;

        // when
        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(1, readyForReviewFlashcards.size());
    }

    @ParameterizedTest
    @MethodSource("dateTimesAfterAll")
    public void findAllReadyForViewCards_WhenParamIsTheLatestOrEqualDateTime(
            LocalDateTime nextLearnViewInUTCBefore) {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();
        int limit = 10;

        // when
        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(2, readyForReviewFlashcards.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    public void findAllReadyForViewByDeck_ShouldLimitResults(int limit) {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDateTime nextLearnViewInUTCBefore = LocalDateTime.of(2023, 3, 17, 14, 18, 43, 0);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();

        // when
        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(limit, readyForReviewFlashcards.size());
    }

    static Stream<LocalDateTime> dateTimesInTheMiddle() {
        return Stream.of(
                LocalDateTime.of(2023, 2, 9, 10, 24, 56),
                LocalDateTime.of(2023, 2, 10, 13, 24, 56),
                LocalDateTime.of(2023, 3, 15, 23, 59, 59, 0));
    }

    static Stream<LocalDateTime> dateTimesAfterAll() {
        return Stream.of(
                LocalDateTime.of(2023, 3, 16, 0, 0, 0, 0),
                LocalDateTime.of(2023, 3, 16, 7, 23, 46, 0),
                LocalDateTime.of(2023, 3, 17, 14, 18, 43, 0));
    }

}