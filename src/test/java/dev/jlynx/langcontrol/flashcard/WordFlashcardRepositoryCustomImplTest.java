//package dev.jlynx.langcontrol.flashcard;
//
//import dev.jlynx.langcontrol.deck.Deck;
//import dev.jlynx.langcontrol.deck.DeckRepository;
//import dev.jlynx.langcontrol.lang.LanguageCode;
//import dev.jlynx.langcontrol.spacedrepetition.LearnModeStep;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Stream;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@DataJpaTest
//class WordFlashcardRepositoryCustomImplTest {
//
//    private static final String deckName = "Test deck";
//    private static final String emptyDeckName = "Empty deck";
//
//
//    @Autowired
//    private WordFlashcardRepositoryCustomImpl underTest;
//
//    @Autowired
//    private DeckRepository deckRepository;
//
//    @Autowired
//    private WordFlashcardRepository wordFlashcardRepository;
//
//    @BeforeEach
//    void setUp() {
//        Deck deck = new Deck();
//        deck.setId(null);
//        deck.setUserProfile(null);
//        deck.setName(deckName);
//        deck.setSourceLang(LanguageCode.ENGLISH);
//        deck.setTargetLang(LanguageCode.GERMAN);
//
//        Deck emptyDeck = new Deck();
//        deck.setId(null);
//        deck.setUserProfile(null);
//        deck.setName(emptyDeckName);
//        deck.setSourceLang(LanguageCode.ENGLISH);
//        deck.setTargetLang(LanguageCode.GERMAN);
//
//        WordFlashcard learnCard = new WordFlashcard();
//        learnCard.setId(null);
//        learnCard.setDeck(deck);
//        learnCard.setSourceLang(LanguageCode.ENGLISH);
//        learnCard.setTargetLang(LanguageCode.GERMAN);
//        learnCard.setTranslatedWord("learn card's front");
//        learnCard.setTargetWord("learn card's back");
//        learnCard.setCreatedAt(LocalDateTime.of(2017, 5, 21, 16, 37));
//        learnCard.setInLearnMode(true);
//        learnCard.setLearnModeStep(LearnModeStep.TWO);
//        learnCard.setNextView(LocalDateTime.of(2023, 2, 9, 10, 24, 56));
//        learnCard.setIFactor(1.3f);
//        learnCard.setRFactor(0.05f);
//
//        WordFlashcard reviewCard = new WordFlashcard();
//        reviewCard.setId(null);
//        reviewCard.setDeck(deck);
//        reviewCard.setSourceLang(LanguageCode.ENGLISH);
//        reviewCard.setTargetLang(LanguageCode.GERMAN);
//        reviewCard.setTranslatedWord("review card's front");
//        reviewCard.setTargetWord("review card's back");
//        reviewCard.setCreatedAt(LocalDateTime.of(2017, 5, 21, 16, 37));
//        reviewCard.setInLearnMode(false);
//        reviewCard.setLearnModeStep(null);
//        reviewCard.setNextView(LocalDateTime.of(2023, 3, 16, 0, 0, 0, 0));
//        reviewCard.setIFactor(1.3f);
//        reviewCard.setRFactor(0.05f);
//
//        deck.setFlashcards(List.of(learnCard, reviewCard));
//        deckRepository.saveAll(List.of(deck, emptyDeck));
//        wordFlashcardRepository.saveAll(List.of(learnCard, reviewCard));
//    }
//
//    @AfterEach
//    void tearDown() {
//        deckRepository.deleteAll();
//        wordFlashcardRepository.deleteAll();
//    }
//
//    @Test
//    public void findAllReadyForViewByDeck_ShouldReturnNoCards_WhenDatetimeTooEarly() {
//        // given
//        Deck deck = deckRepository.findByName(deckName).orElseThrow(IllegalStateException::new);
//        LocalDateTime tooEarlyTimestamp = LocalDateTime.of(2023, 2, 7, 8, 13);
//
//        // when
//        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck, tooEarlyTimestamp);
//
//        // then
//        assertEquals(0, readyForReviewFlashcards.size());
//    }
//
//    @ParameterizedTest
//    @MethodSource("datetimesInTheMiddle")
//    public void findAllReadyForViewByDeck_ShouldReturnOnlyCardsScheduledEarlierOrEqualToTimestamp(LocalDateTime timestamp) {
//        // given
//        Deck deck = deckRepository.findByName(deckName).orElseThrow(IllegalStateException::new);
//
//        // when
//        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck, timestamp);
//
//        // then
//        assertEquals(1, readyForReviewFlashcards.size());
//    }
//
//    @ParameterizedTest
//    @MethodSource("datetimesAfterAll")
//    public void findAllReadyForViewByDeck_ShouldReturnAll_WhenTimestampIsTheLatest(LocalDateTime timestamp) {
//        // given
//        Deck deck = deckRepository.findByName(deckName).orElseThrow(IllegalStateException::new);
//
//        // when
//        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(deck, timestamp);
//
//        // then
//        assertEquals(2, readyForReviewFlashcards.size());
//    }
//
//    @ParameterizedTest
//    @MethodSource({"datetimesInTheMiddle", "datetimesAfterAll"})
//    public void findAllReadyForViewByDeck_ShouldReturnZeroCards_WhenDeckIsEmpty(LocalDateTime timestamp) {
//        // given
//        Deck emptyDeck = deckRepository.findByName(emptyDeckName).orElseThrow(IllegalStateException::new);
//
//        // when
//        List<WordFlashcard> readyForReviewFlashcards = underTest.findAllReadyForViewByDeck(emptyDeck, timestamp);
//
//        // then
//        assertTrue(readyForReviewFlashcards.isEmpty());
//    }
//
//    static Stream<LocalDateTime> datetimesInTheMiddle() {
//        return Stream.of(
//                LocalDateTime.of(2023, 2, 9, 10, 24, 56),
//                LocalDateTime.of(2023, 2, 10, 13, 24, 56),
//                LocalDateTime.of(2023, 3, 15, 23, 59, 59, 0)
//        );
//    }
//
//    static Stream<LocalDateTime> datetimesAfterAll() {
//        return Stream.of(
//                LocalDateTime.of(2023, 3, 16, 0, 0, 0, 0),
//                LocalDateTime.of(2023, 3, 16, 7, 23, 46, 0),
//                LocalDateTime.of(2023, 3, 17, 14, 18, 43, 0)
//        );
//    }
//}