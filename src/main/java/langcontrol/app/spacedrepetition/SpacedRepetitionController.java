package langcontrol.app.spacedrepetition;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import langcontrol.app.flashcard.WordFlashcard;
import langcontrol.app.flashcard.WordFlashcardService;
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

    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public SpacedRepetitionController(WordFlashcardService wordFlashcardService) {
        this.wordFlashcardService = wordFlashcardService;
    }

    @GetMapping("/review")
    public String openDeck(@Min(1) @RequestParam("deckId") long deckId,
                           @NotBlank @RequestParam("timezone") String timezoneId,
                           Model model, SessionStatus status) {
//        status.setComplete();
        Deque<WordFlashcard> readyForReview = wordFlashcardService.fetchShuffleReadyForView(deckId, 10);
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
    public String reviewNextCard(@ModelAttribute("reviewCards") Deque<WordFlashcard> readyForReview,
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
