package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckRepository;
import langcontrol.app.deck.LanguageCode;
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
class FlashcardRepositoryCustomImplTest {

    @Autowired
    private FlashcardRepositoryCustomImpl underTest;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @BeforeEach
    void setUp() {
        Deck deck = new Deck();
        deck.setId(null);
        deck.setUserProfile(null);
        deck.setName("test deck");
        deck.setSourceLanguage(LanguageCode.ENGLISH);
        deck.setTargetLanguage(LanguageCode.GERMAN);

        Flashcard learnCard = new Flashcard();
        learnCard.setId(null);
        learnCard.setDeck(deck);
        learnCard.setSourceLanguage(LanguageCode.ENGLISH);
        learnCard.setTargetLanguage(LanguageCode.GERMAN);
        learnCard.setFront("learn card's front");
        learnCard.setBack("learn card's back");
        learnCard.setCreationDateTimeInUTC(LocalDateTime.of(2017, 5, 21, 16, 37));
        learnCard.setInLearnMode(true);
        learnCard.setLearnModeStep(LearnModeStep.TWO);
        learnCard.setNextLearnViewInUTC(LocalDateTime.of(2023, 2, 9, 10, 24, 56));
        learnCard.setIncreaseFactor(1.3);
        learnCard.setReduceFactor(0.7);

        Flashcard reviewCard = new Flashcard();
        reviewCard.setId(null);
        reviewCard.setDeck(deck);
        reviewCard.setSourceLanguage(LanguageCode.ENGLISH);
        reviewCard.setTargetLanguage(LanguageCode.GERMAN);
        reviewCard.setFront("review card's front");
        reviewCard.setBack("review card's back");
        reviewCard.setCreationDateTimeInUTC(LocalDateTime.of(2017, 5, 21, 16, 37));
        reviewCard.setInLearnMode(false);
        reviewCard.setLearnModeStep(null);
        reviewCard.setNextLearnViewInUTC(null);
        reviewCard.setNextReviewInUTC(LocalDateTime.of(2023, 3, 16, 0, 0, 0, 0));
        reviewCard.setNextReviewWithoutTimeInUTC(reviewCard.getNextReviewInUTC().toLocalDate());
        reviewCard.setIncreaseFactor(1.3);
        reviewCard.setReduceFactor(0.7);

        deck.setFlashcards(List.of(learnCard, reviewCard));
        deckRepository.save(deck);
    }

    @AfterEach
    void tearDown() {
        deckRepository.deleteAll();
        flashcardRepository.deleteAll();
    }

    @Test
    public void findReadyForReviewFlashcardsByDeck_ShouldReturnNoFlashcards_WhenDateTimesAreTooEarly() {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDateTime nextLearnViewInUTCBefore = LocalDateTime.of(2023, 2, 7, 8, 13);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();
        int limit = 10;

        // when
        List<Flashcard> readyForReviewFlashcards = underTest.findReadyForReviewFlashcardsByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(0, readyForReviewFlashcards.size());
    }

    @ParameterizedTest
    @MethodSource("dateTimesInTheMiddle")
    public void findReadyForReviewFlashcardsByDeck_ShouldReturnOnlyFlashcardsScheduledForEarlierOrEqualToParam(
            LocalDateTime nextLearnViewInUTCBefore) {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();
        int limit = 10;

        // when
        List<Flashcard> readyForReviewFlashcards = underTest.findReadyForReviewFlashcardsByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(1, readyForReviewFlashcards.size());
    }

    @ParameterizedTest
    @MethodSource("dateTimesAfterAll")
    public void findReadyForReviewFlashcardsByDeck_ShouldReturnAllFlashcards_WhenParamIsTheLatestOrEqualDateTime(
            LocalDateTime nextLearnViewInUTCBefore) {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();
        int limit = 10;

        // when
        List<Flashcard> readyForReviewFlashcards = underTest.findReadyForReviewFlashcardsByDeck(deck,
                nextLearnViewInUTCBefore, nextReviewDateLocalBefore, limit);

        // then
        assertEquals(2, readyForReviewFlashcards.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    public void findReadyForReviewFlashcardsByDeck_ShouldLimitResults(int limit) {
        // given
        Deck deck = deckRepository.findByName("test deck").orElseThrow(IllegalStateException::new);
        LocalDateTime nextLearnViewInUTCBefore = LocalDateTime.of(2023, 3, 17, 14, 18, 43, 0);
        LocalDate nextReviewDateLocalBefore = nextLearnViewInUTCBefore.toLocalDate();

        // when
        List<Flashcard> readyForReviewFlashcards = underTest.findReadyForReviewFlashcardsByDeck(deck,
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