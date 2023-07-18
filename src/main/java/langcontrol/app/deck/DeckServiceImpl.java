package langcontrol.app.deck;

import langcontrol.app.deck.rest.DeckDetailsDTO;
import langcontrol.app.exception.AccessNotAllowedException;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.exception.DeckCreationException;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.flashcard.FlashcardService;
import langcontrol.app.account.Account;
import langcontrol.app.user_profile.UserProfile;
import langcontrol.app.user_profile.UserProfileService;
import langcontrol.app.util.PrincipalRetriever;
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
    private final FlashcardService flashcardService;

    @Autowired
    public DeckServiceImpl(DeckRepository deckRepository, UserProfileService userProfileService,
                           FlashcardService flashcardService) {
        this.deckRepository = deckRepository;
        this.userProfileService = userProfileService;
        this.flashcardService = flashcardService;
    }

    @Transactional
    @Override
    public void createNewDeck(Deck deckToCreate) {
        if (deckToCreate.getSourceLanguage() == deckToCreate.getTargetLanguage()) {
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
        Account currentAccount = PrincipalRetriever.retrieveAccount();

        Deck foundDeck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(foundDeck.getUserProfile().getId(), currentAccount.getUserProfile().getId())) {
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
        Account currentAccount = PrincipalRetriever.retrieveAccount();
        Deck deck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(deck.getUserProfile().getId(), currentAccount.getUserProfile().getId())) {
            throw new AccessNotAllowedException("You don't have permission to view this data.");
        }
        int totalCardsNum = deck.getFlashcards().size();
        Deque<Flashcard> cardsForReview = flashcardService.fetchReadyForReviewShuffledWithLimit(deckId,
                zoneId, Integer.MAX_VALUE);
        int cardsForReviewNum = cardsForReview.size();

        return new DeckDetailsDTO(deck.getId(), deck.getName(), totalCardsNum, cardsForReviewNum);
    }


}
