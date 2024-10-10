package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.dto.DeckDetails;
import dev.jlynx.langcontrol.deck.dto.UpdateDeckRequest;
import dev.jlynx.langcontrol.deck.view.DeckView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("${apiPref}/decks")
public class DeckController {

    private final DeckService deckService;

    @Autowired
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping
    public ResponseEntity<DeckOverview> createDeck(@Valid @RequestBody CreateDeckRequest body) {
        DeckOverview response = deckService.createNewDeck(body);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeckView>> getAllUserDecks() {
        List<DeckView> decks = deckService.getAllCurrentUserProfileDecks();
        return ResponseEntity.ok(decks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeckOverview> getDeck(@Min(1) @PathVariable("id") long deckId) {
        DeckOverview deckOverview = deckService.getDeckById(deckId);
        return ResponseEntity.ok(deckOverview);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DeckDetails> getDeckDetails(@Min(1) @PathVariable("id") long deckId) {
        DeckDetails deckDetails = deckService.extractDeckDetails(deckId);
        return ResponseEntity.ok(deckDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDeck(
            @Min(1) @PathVariable("id") long deckId,
            @RequestBody @Valid UpdateDeckRequest body
    ) {
        deckService.updateDeck(deckId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@Min(1) @PathVariable("id") long deckId) {
        deckService.deleteDeck(deckId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
