package langcontrol.app.spaced_repetition;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.flashcard.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Deque;
import java.util.Objects;

@Validated
@SessionAttributes("reviewCards")
//@Scope("session")
@Controller
public class SpacedRepetitionController {

    private final FlashcardService flashcardService;

    @Autowired
    public SpacedRepetitionController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @GetMapping("/review")
    public String openDeck(@Min(1) @RequestParam("deckId") long deckId,
                           @NotBlank @RequestParam("timezone") String timezoneId,
                           Model model, SessionStatus status) {
//        status.setComplete();
        Deque<Flashcard> readyForReview = flashcardService.fetchReadyForReviewShuffledWithLimit(deckId, timezoneId, 10);
        if (readyForReview.isEmpty()) {
            return "redirect:/decks";
        }
        model.addAttribute("reviewCards", readyForReview);
        model.addAttribute("currentCard", readyForReview.element());
//        model.addAttribute("rating", new FlashcardRatingDTO());
        if (readyForReview.getFirst().isInLearnMode()) {
            return "learn";
        } else {
            return "review";
        }
    }

    @GetMapping("/review/next")
    public String reviewNextCard(@ModelAttribute("reviewCards") Deque<Flashcard> readyForReview,
                                 Model model) {
        if (Objects.isNull(readyForReview) || readyForReview.isEmpty()) {
            return "redirect:/decks";
        }
        model.addAttribute("currentCard", readyForReview.element());
        model.addAttribute("rating", new FlashcardRatingDTO());
        if (readyForReview.element().isInLearnMode()) {
            return "learn";
        } else {
            return "review";
        }
    }

}
