package langcontrol.app.deck;

import langcontrol.app.deck.rest.DeckDetailsDTO;

import java.util.List;

public interface DeckService {

    void createNewDeck(Deck deckToCreate);

    List<DeckView> getAllDecks();

    Deck getDeckById(Long deckId);

    void deleteDeck(long deckId);

    DeckDetailsDTO extractDeckDetails(long deckId, String zoneId);

}
