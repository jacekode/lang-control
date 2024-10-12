package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardView;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingRequest;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;
import dev.jlynx.langcontrol.url.SortOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("${apiPref}/sr")
@RestController
public class SpacedRepetitionController {

    private final WordFlashcardService wordFlashcardService;
    private final SpacedRepetitionService spacedRepetitionService;

    @Autowired
    public SpacedRepetitionController(
            WordFlashcardService wordFlashcardService,
            SpacedRepetitionService spacedRepetitionService
    ) {
        this.wordFlashcardService = wordFlashcardService;
        this.spacedRepetitionService = spacedRepetitionService;
    }

    @GetMapping
    public ResponseEntity<?> getReadyForViewCardsByDeck(
            @Min(1) @RequestParam("deck") long deckId,
            @Min(3) @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false, defaultValue = "view") SpacedRepetitionSortBy sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") SortOrder order
    ) {
        List<WordFlashcardView> readyCards = wordFlashcardService.fetchReadyForView(deckId, limit, sortBy, order);
        return ResponseEntity.ok(readyCards);
    }

    @PostMapping("/rating")
    public ResponseEntity<FlashcardRatingResponse> handleFlashcardRating(@Valid @RequestBody FlashcardRatingRequest body) {
        FlashcardRatingResponse flashcardRatedOverview = spacedRepetitionService.applyRating(
                body.cardId(),
                body.rating()
        );
        return ResponseEntity.ok(flashcardRatedOverview);
    }
}
