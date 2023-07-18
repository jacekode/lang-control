package langcontrol.app.deck.rest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/api/decks")
@RestController
public class DeckRestController {

    private final DeckService deckService;

    @Autowired
    public DeckRestController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping("/{id}/details")
    ResponseEntity<DeckDetailsDTO> getDeckDetails(@Min(1) @PathVariable("id") long deckId,
                                                  @NotBlank @RequestParam("timezone") String timezoneId) {
        Deck deck = deckService.getDeckById(deckId);
        DeckDetailsDTO deckDetailsDto = deckService.extractDeckDetails(deckId, timezoneId);
        return ResponseEntity.ok(deckDetailsDto);
    }
}
