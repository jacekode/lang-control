package langcontrol.app.spacedrepetition.rest;

import jakarta.validation.Valid;
import langcontrol.app.flashcard.WordFlashcard;
import langcontrol.app.spacedrepetition.FlashcardRatedOverviewDTO;
import langcontrol.app.spacedrepetition.FlashcardRatingDTO;
import langcontrol.app.spacedrepetition.SpacedRepetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayDeque;

@Validated
@SessionAttributes("reviewCards")
@RequestMapping("/api")
@RestController
public class SpacedRepetitionRestController {

    private final SpacedRepetitionService spacedRepetitionService;

    @Autowired
    public SpacedRepetitionRestController(SpacedRepetitionService spacedRepetitionService) {
        this.spacedRepetitionService = spacedRepetitionService;
    }

    @PostMapping(value = "/rating")
    public ResponseEntity<Object> handleFlashcardRating(@Valid @ModelAttribute("rating")
                                                        FlashcardRatingDTO rating,
                                                        @ModelAttribute("reviewCards")
                                                        ArrayDeque<WordFlashcard> readyForReview) {
        FlashcardRatedOverviewDTO flashcardRatedOverview = spacedRepetitionService
                .applyRating(rating.getFlashcardId(), rating.getRatingType());
        readyForReview.poll();
        return ResponseEntity.ok(flashcardRatedOverview);
    }
}
