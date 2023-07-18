package langcontrol.app.flashcard;

import langcontrol.app.user_profile.UserProfile;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckRepository;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.deck.LanguageCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class FlashcardServiceImplTest {

    private FlashcardRepository mockedFlashcardRepository;
    private DeckRepository mockedDeckRepository;
    private FlashcardServiceImpl underTest;

    @BeforeEach
    void setUp() {
        mockedFlashcardRepository = Mockito.mock(FlashcardRepository.class);
        mockedDeckRepository = Mockito.mock(DeckRepository.class);
        underTest = new FlashcardServiceImpl(mockedFlashcardRepository, mockedDeckRepository, deckService, dictionary);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 0, -1})
    void deleteFlashcard_ShouldDeleteTheFlashcardWithSpecifiedId(long id) {
        // when
        underTest.deleteFlashcard(id);

        // then
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockedFlashcardRepository).deleteById(argumentCaptor.capture());
        assertEquals(id, argumentCaptor.getValue());
    }

//    @ParameterizedTest
//    @ValueSource(longs = {-1, 0})
//    void deleteFlashcard_ShouldThrowException_WhenIdIsLessThanZero(long id) {
//        // then
//        assertThrows(IllegalArgumentException.class,
//                () -> underTest.deleteFlashcard(id));
//    }

    @Test
    void createNewFlashcard_ShouldCreateFlashcard_WhenDeckIsFound() {
        // given
        long deckId = 2L;
        FlashcardCreationDTO creationDTO = new FlashcardCreationDTO("test front", "test back");
        Deck mockedDeck = Mockito.mock(Deck.class);
        when(mockedDeckRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockedDeck));

        // when
        underTest.createNewFlashcard(deckId, creationDTO);

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Flashcard> flashcardArgumentCaptor = ArgumentCaptor.forClass(Flashcard.class);

        InOrder inOrder = Mockito.inOrder(mockedDeckRepository, mockedDeck);
        inOrder.verify(mockedDeckRepository).findById(longArgumentCaptor.capture());
        inOrder.verify(mockedDeck).addFlashcard(flashcardArgumentCaptor.capture());

        assertEquals(deckId, longArgumentCaptor.getValue());
        assertEquals("test front", flashcardArgumentCaptor.getValue().getFront());
        assertEquals("test back", flashcardArgumentCaptor.getValue().getBack());
    }

    @Test
    void createNewFlashcard_ShouldThrowException_WhenDeckIsNotFound() {
        // given
        long deckId = 2L;
        FlashcardCreationDTO creationDTO = new FlashcardCreationDTO("test front", "test back");
        when(mockedDeckRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(GeneralNotFoundException.class,
                () -> underTest.createNewFlashcard(deckId, creationDTO));
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldReturnFlashcards() {
        // given
        String zoneId = "America/Los_Angeles";
        long deckId = 2;
        int limit = 15;
        LocalDateTime locDateTimeInUtcExpected = LocalDateTime.now(Clock.systemUTC());
        LocalDate locDateExpected = ZonedDateTime.now(ZoneId.of(zoneId)).toLocalDateTime().toLocalDate();

        Deck testDeck = new Deck(deckId,
                "test deck",
                new UserProfile(),
                LanguageCode.SPANISH,
                LanguageCode.ENGLISH,
                new ArrayList<>());

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testDeck));

        // when
        Deque<Flashcard> result = underTest.fetchReadyForReviewShuffledWithLimit(deckId, zoneId, limit);

        // then
        InOrder inOrder = Mockito.inOrder(mockedDeckRepository, mockedFlashcardRepository);
        ArgumentCaptor<LocalDateTime> locDateTimeArgCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDate> locDateArgCaptor = ArgumentCaptor.forClass(LocalDate.class);

        then(mockedDeckRepository).should(inOrder).findById(deckId);
        then(mockedFlashcardRepository).should(inOrder).findReadyForReviewFlashcardsByDeck(
                eq(testDeck),
                locDateTimeArgCaptor.capture(),
                locDateArgCaptor.capture(),
                eq(limit));

        LocalDateTime locDateTimeInUtcArg = locDateTimeArgCaptor.getValue();
        LocalDate locDateArg = locDateArgCaptor.getValue();

        assertAll("LocalDateTime in UTC assertions",
                () -> assertEquals(locDateTimeInUtcExpected.getYear(), locDateTimeInUtcArg.getYear()),
                () -> assertEquals(locDateTimeInUtcExpected.getMonth(), locDateTimeInUtcArg.getMonth()),
                () -> assertEquals(locDateTimeInUtcExpected.getDayOfMonth(), locDateTimeInUtcArg.getDayOfMonth()),
                () -> assertEquals(locDateTimeInUtcExpected.getHour(), locDateTimeInUtcArg.getHour()),
                () -> assertEquals(locDateTimeInUtcExpected.getMinute(), locDateTimeInUtcArg.getMinute()),
                () -> assertEquals(locDateTimeInUtcExpected.getSecond(), locDateTimeInUtcArg.getSecond())
        );
        assertAll("LocalDate assertions",
                () -> assertEquals(locDateExpected.getYear(), locDateArg.getYear()),
                () -> assertEquals(locDateExpected.getMonth(), locDateArg.getMonth()),
                () -> assertEquals(locDateExpected.getDayOfMonth(), locDateArg.getDayOfMonth())
        );
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldThrowException_WhenDeckNotFound() {
        // given
        String zoneId = "America/Los_Angeles";
        long deckId = 2;
        int limit = 15;
        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        // then
        assertThrows(GeneralNotFoundException.class,
                () -> underTest.fetchReadyForReviewShuffledWithLimit(deckId, zoneId, limit));
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldThrowException_WhenLimitIsLessThanZero() {
        // given
        String zoneId = "America/Los_Angeles";
        long deckId = 2;
        int limit = -20;

        // then
        assertThrows(IllegalArgumentException.class,
                () -> underTest.fetchReadyForReviewShuffledWithLimit(deckId, zoneId, limit));
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldThrowException_WhenZoneIdIsNotCorrect() {
        // given
        String zoneId = "American/Los_Angeles";
        long deckId = 2;
        int limit = 15;

        // then
        assertThrows(IllegalArgumentException.class,
                () -> underTest.fetchReadyForReviewShuffledWithLimit(deckId, zoneId, limit));
    }

    @Test
    void getAllFlashcardsByDeck_ShouldGetFlashcards() {
        // given
        Deck testDeck = new Deck(2L,
                "test deck",
                new UserProfile(),
                LanguageCode.SPANISH,
                LanguageCode.ENGLISH,
                new ArrayList<>());

        // when
        underTest.getAllFlashcardsByDeck(testDeck);

        // then
        ArgumentCaptor<Deck> deckArgumentCaptor = ArgumentCaptor.forClass(Deck.class);
        verify(mockedFlashcardRepository).findByDeck(deckArgumentCaptor.capture());
        assertEquals(testDeck, deckArgumentCaptor.getValue());
    }
}
