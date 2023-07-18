package langcontrol.app.flashcard.rest;

import jakarta.validation.constraints.Min;
import langcontrol.app.flashcard.FlashcardService;
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

    private final FlashcardService flashcardService;

    @Autowired
    public FlashcardRestController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @GetMapping("/cards/{id}/forecasts")
    ResponseEntity<FlashcardForecastsDTO> getReviewTimeForecasts(@Min(1) @PathVariable("id") long flashcardId) {
        FlashcardForecastsDTO forecasts = flashcardService.produceReviewTimeForecastsAsText(flashcardId);
        return ResponseEntity.ok(forecasts);
    }

}
