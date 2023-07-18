package langcontrol.app.spaced_repetition.rest;

import jakarta.validation.Valid;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.spaced_repetition.FlashcardRatedOverviewDTO;
import langcontrol.app.spaced_repetition.FlashcardRatingDTO;
import langcontrol.app.spaced_repetition.SpacedRepetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    public ResponseEntity<Object> handleFlashcardRating(@Valid @ModelAttribute("rating") FlashcardRatingDTO rating,
                                                        @ModelAttribute("reviewCards") ArrayDeque<Flashcard> readyForReview) {
        FlashcardRatedOverviewDTO flashcardRatedOverview = spacedRepetitionService
                .applyRating(rating.getFlashcardId(), rating.getRatingType());
        readyForReview.poll();
        return ResponseEntity.ok(flashcardRatedOverview);
    }
}
