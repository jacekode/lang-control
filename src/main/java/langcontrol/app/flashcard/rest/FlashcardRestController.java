package langcontrol.app.flashcard.rest;

import jakarta.validation.constraints.Min;
import langcontrol.app.flashcard.IntervalForecastDTO;
import langcontrol.app.flashcard.WordFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api")
@RestController
public class FlashcardRestController {

    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public FlashcardRestController(WordFlashcardService wordFlashcardService) {
        this.wordFlashcardService = wordFlashcardService;
    }

    @GetMapping("/cards/{id}/forecasts")
    ResponseEntity<IntervalForecastDTO> getReviewTimeForecasts(@Min(1) @PathVariable("id") long flashcardId) {
        IntervalForecastDTO forecasts = wordFlashcardService.produceReviewTimeForecastsAsText(flashcardId);
        return ResponseEntity.ok(forecasts);
    }

}
