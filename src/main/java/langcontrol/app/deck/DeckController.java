package langcontrol.app.deck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import langcontrol.app.flashcard.WordFlashcard;
import langcontrol.app.flashcard.WordFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Controller
public class DeckController {

    private final DeckService deckService;
    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public DeckController(DeckService deckService, WordFlashcardService wordFlashcardService) {
        this.deckService = deckService;
        this.wordFlashcardService = wordFlashcardService;
    }

    @GetMapping("/add-deck")
    public String getCreateDeckPage(Model model) {
        model.addAttribute("deckToCreate", new Deck());
        model.addAttribute("languageCodes", LanguageCode.values());
        return "add-deck";
    }

    @PostMapping("/add-deck")
    public String createDeck(@Valid @ModelAttribute("deckToCreate") CreateDeckDTO createDeckDto) {
        Deck deckToCreate = new Deck(createDeckDto);
        deckService.createNewDeck(deckToCreate);
        return "redirect:/add-deck";
    }

    @GetMapping("/decks")
    public String getAllDecksPage(Model model) {
        List<DeckView> allDecks = deckService.getAllDecks();
        model.addAttribute("decks", allDecks);
        return "all-decks";
    }

    @PostMapping("/deck/{id}/delete")
    public String deleteDeck(@Min(1) @PathVariable("id") long deckIdToDelete) {
        deckService.deleteDeck(deckIdToDelete);
        return "redirect:/decks";
    }

    @GetMapping("/deck/{id}/cards")
    public String showAllDecksFlashcards(@Min(1) @PathVariable("id") long deckId, Model model) {
        Deck foundDeck = deckService.getDeckById(deckId);
        List<WordFlashcard> deckFlashcards = wordFlashcardService.getAllFlashcardsByDeck(foundDeck);
        model.addAttribute("deck", foundDeck);
        model.addAttribute("deckFlashcards", deckFlashcards);
        return "all-deck-cards";
    }
}
