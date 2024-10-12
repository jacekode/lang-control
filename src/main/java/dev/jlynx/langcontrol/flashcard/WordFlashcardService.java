package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.flashcard.dto.*;
import dev.jlynx.langcontrol.spacedrepetition.SpacedRepetitionSortBy;
import dev.jlynx.langcontrol.url.SortOrder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface WordFlashcardService {

    WordFlashcardOverviewResponse createNewFlashcard(CreateWordFlashcardRequest body);

    WordFlashcardOverviewResponse createNewFlashcardZenMode(CreateWordFlashcardZenModeRequest body);

    WordFlashcard getCardById(long id);

    WordFlashcardOverviewResponse getCardOverviewById(long id);

    Page<WordFlashcardView> getFlashcards(Optional<Long> deckId, int pageNum, int pageSize, FlashcardSortBy sortBy, SortOrder order);

    void updateFlashcard(long cardId, UpdateWordFlashcardRequest body);

    void deleteFlashcard(long flashcardId);

    List<WordFlashcardView> fetchAllReadyForViewCardsByDeck(long deckId);

    List<WordFlashcardView> fetchReadyForView(long deckId, int limit, SpacedRepetitionSortBy sortBy, SortOrder order);
    
    IntervalForecastResponse getIntervalForecasts(long cardId);
}
