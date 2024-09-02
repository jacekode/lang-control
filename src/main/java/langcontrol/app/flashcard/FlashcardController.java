package langcontrol.app.flashcard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckService;
import langcontrol.app.usersettings.UserSettings;
import langcontrol.app.usersettings.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Controller
public class FlashcardController {

    private final WordFlashcardService wordFlashcardService;
    private final DeckService deckService;
    private final UserSettingsService userSettingsService;

    @Autowired
    public FlashcardController(WordFlashcardService wordFlashcardService,
                               DeckService deckService,
                               UserSettingsService userSettingsService) {
        this.wordFlashcardService = wordFlashcardService;
        this.deckService = deckService;
        this.userSettingsService = userSettingsService;
    }

    @PostMapping("/card/{id}/delete")
    public String deleteFlashcard(@Min(1) @PathVariable("id") long flashcardId,
                                  @Min(1) @RequestParam("deckId") long deckId) {
        wordFlashcardService.deleteFlashcard(flashcardId);
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
        wordFlashcardService.createNewFlashcard(deckId, createCardDto);
        return "redirect:/add-card?deckid=" + deckId;
    }

    @PostMapping("/add-card/zenmode")
    public String createFlashcardWithZenMode(@Min(1) @RequestParam("deckid") long deckId,
                                             @Valid @ModelAttribute("newCardZenMode") FlashcardZenModeCreationDTO dto) {
        wordFlashcardService.createNewFlashcardZenMode(deckId, dto);
        return "redirect:/add-card?deckid=" + deckId;
    }
}
