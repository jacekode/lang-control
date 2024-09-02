package langcontrol.app.deck;

import langcontrol.app.deck.rest.DeckDetailsDTO;
import langcontrol.app.exception.AccessNotAllowedException;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.exception.DeckCreationException;
import langcontrol.app.flashcard.WordFlashcard;
import langcontrol.app.flashcard.WordFlashcardService;
import langcontrol.app.userprofile.UserProfile;
import langcontrol.app.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

@Service
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final UserProfileService userProfileService;
    private final WordFlashcardService wordFlashcardService;

    @Autowired
    public DeckServiceImpl(DeckRepository deckRepository, UserProfileService userProfileService,
                           WordFlashcardService wordFlashcardService) {
        this.deckRepository = deckRepository;
        this.userProfileService = userProfileService;
        this.wordFlashcardService = wordFlashcardService;
    }

    @Transactional
    @Override
    public void createNewDeck(Deck deckToCreate) {
        if (deckToCreate.getSourceLang() == deckToCreate.getTargetLang()) {
            throw new DeckCreationException("Source language and target language cannot be the same.");
        }
        UserProfile currentUserProfile = userProfileService.retrieveCurrentUserProfile();

        deckToCreate.setId(null);
        deckToCreate.setFlashcards(new ArrayList<>());
        deckToCreate.setUserProfile(currentUserProfile);
        currentUserProfile.getDecks().add(deckToCreate);
    }

    @Transactional
    @Override
    public List<DeckView> getAllDecks() {
        UserProfile currentUserProfile = userProfileService.retrieveCurrentUserProfile();
        return deckRepository.findByUserProfile(currentUserProfile);
    }

    @Transactional
    @Override
    public Deck getDeckById(Long deckId) {
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        Deck foundDeck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(foundDeck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessNotAllowedException("You don't have permission to perform this action.");
        }
        return foundDeck;
    }

    @Transactional
    @Override
    public void deleteDeck(long deckId) {
        Deck foundDeck = this.getDeckById(deckId);
        deckRepository.delete(foundDeck);
    }

    @Transactional
    @Override
    public DeckDetailsDTO extractDeckDetails(long deckId, String zoneId) {
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        Deck deck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(deck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessNotAllowedException("You don't have permission to view this data.");
        }
        int totalCardNum = deck.getFlashcards().size();
        Deque<WordFlashcard> readyForView = wordFlashcardService.fetchShuffleReadyForView(
                deckId, Integer.MAX_VALUE
        );
        int readyForViewNum = readyForView.size();
        return new DeckDetailsDTO(deck.getId(), deck.getName(), totalCardNum, readyForViewNum);
    }
}
