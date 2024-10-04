package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.dto.DeckDetails;
import dev.jlynx.langcontrol.deck.dto.UpdateDeckRequest;
import dev.jlynx.langcontrol.deck.view.DeckView;

import java.util.List;

public interface DeckService {

    DeckOverview createNewDeck(CreateDeckRequest body);

    List<DeckView> getAllCurrentUserProfileDecks();

    DeckOverview getDeckById(long deckId);

    void updateDeck(long deckId, UpdateDeckRequest body);

    void deleteDeck(long deckId);

    DeckDetails extractDeckDetails(long deckId);
}
