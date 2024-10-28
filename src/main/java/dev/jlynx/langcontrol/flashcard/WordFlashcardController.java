package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.flashcard.dto.*;
import dev.jlynx.langcontrol.url.SortOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Validated
@RequestMapping("${apiPref}/cards")
@RestController
public class WordFlashcardController {

    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public WordFlashcardController(WordFlashcardService wordFlashcardService) {
        this.wordFlashcardService = wordFlashcardService;
    }

    @PostMapping
    public ResponseEntity<WordFlashcardOverviewResponse> createFlashcard(@RequestBody @Valid CreateWordFlashcardRequest body) {
        var cardOverview = wordFlashcardService.createNewFlashcard(body);
        return new ResponseEntity<>(cardOverview, HttpStatus.CREATED);
    }

    @PostMapping("/zen-mode")
    public ResponseEntity<WordFlashcardOverviewResponse> createFlashcardInZenMode(@RequestBody @Valid CreateWordFlashcardZenModeRequest body) {
        var cardOverview = wordFlashcardService.createNewFlashcardZenMode(body);
        return new ResponseEntity<>(cardOverview, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<WordFlashcardView>> getCardsPaginated(
            @RequestParam(name = "deck", required = false) @Positive Long deckId,
            @RequestParam(name = "page", required = false, defaultValue = "0") @PositiveOrZero int pageNum,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(5) @Max(100) int pageSize,
            @RequestParam(name = "sort", required = false, defaultValue = "created") FlashcardSortBy sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") SortOrder order
            ) {
        var cards = wordFlashcardService.getFlashcards(Optional.ofNullable(deckId), pageNum, pageSize, sortBy, order);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordFlashcardOverviewResponse> getCardById(@Min(1) @PathVariable("id") long cardId) {
        var cardOverview = wordFlashcardService.getCardOverviewById(cardId);
        return ResponseEntity.ok(cardOverview);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFlashcard(
            @Min(1) @PathVariable("id") long cardId,
            @RequestBody @Valid UpdateWordFlashcardRequest body
    ) {
        wordFlashcardService.updateFlashcard(cardId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@Min(1) @PathVariable("id") long cardId) {
        wordFlashcardService.deleteFlashcard(cardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/forecasts")
    ResponseEntity<IntervalForecastResponse> getIntervalForecasts(@Min(1) @PathVariable("id") long cardId) {
        IntervalForecastResponse forecasts = wordFlashcardService.getIntervalForecasts(cardId);
        return ResponseEntity.ok(forecasts);
    }
}
