package langcontrol.app.flashcard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckService;
import langcontrol.app.user_settings.UserSettings;
import langcontrol.app.user_settings.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Controller
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final DeckService deckService;
    private final UserSettingsService userSettingsService;

    @Autowired
    public FlashcardController(FlashcardService flashcardService, DeckService deckService, UserSettingsService userSettingsService) {
        this.flashcardService = flashcardService;
        this.deckService = deckService;
        this.userSettingsService = userSettingsService;
    }

    @PostMapping("/card/{id}/delete")
    public String deleteFlashcard(@Min(1) @PathVariable("id") long flashcardId,
                                  @Min(1) @RequestParam("deckId") long deckId) {
        flashcardService.deleteFlashcard(flashcardId);
        return "redirect:/deck/" + deckId + "/cards";
    }

    @GetMapping("/add-card")
    public String getAddCardPage(@Min(1) @RequestParam("deckid") long deckId, Model model) {
        Deck foundDeck = deckService.getDeckById(deckId);
        UserSettings settings = userSettingsService.retrieveCurrentUserSettings();
        model.addAttribute("deck", foundDeck);
        model.addAttribute("newFlashcard", FlashcardCreationDTO.withUserSettings(settings));
        model.addAttribute("newCardZenMode", new FlashcardZenModeCreationDTO());
        model.addAttribute("userSettings", settings);
        return "add-card";
    }

    @PostMapping("/add-card")
    public String createFlashcard(@Min(1) @RequestParam("deckid") long deckId,
                                     @Valid @ModelAttribute("newFlashcard") FlashcardCreationDTO createCardDto) {
        flashcardService.createNewFlashcard(deckId, createCardDto);
        return "redirect:/add-card?deckid=" + deckId;
    }

    @PostMapping("/add-card/zenmode")
    public String createFlashcardWithZenMode(@Min(1) @RequestParam("deckid") long deckId,
                                             @Valid @ModelAttribute("newCardZenMode") FlashcardZenModeCreationDTO dto) {
        flashcardService.createNewFlashcardZenMode(deckId, dto);
        return "redirect:/add-card?deckid=" + deckId;
    }
}
