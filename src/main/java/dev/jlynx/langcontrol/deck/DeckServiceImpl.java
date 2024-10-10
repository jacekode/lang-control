package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.dto.DeckDetails;
import dev.jlynx.langcontrol.deck.dto.UpdateDeckRequest;
import dev.jlynx.langcontrol.deck.view.DeckView;
import dev.jlynx.langcontrol.exception.AccessForbiddenException;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.exception.DeckCreationException;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardView;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DeckServiceImpl implements DeckService {

    private static final Logger LOG = LoggerFactory.getLogger(DeckServiceImpl.class);

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
    public DeckOverview createNewDeck(CreateDeckRequest body) {
        if (body.sourceLang() == body.targetLang()) {
            LOG.debug("Attempted to create a deck with name='{}' with equal language codes '{}' on both sides. Operation threw exception",
                    body.name(), body.sourceLang());
            throw new DeckCreationException("Source language and target language cannot be the same.");
        }
        UserProfile currentUserProfile = userProfileService.retrieveCurrentProfileEntity();

        Deck deckToCreate = Deck.fromRequest(body);
        deckToCreate.setId(null);
        deckToCreate.setFlashcards(new ArrayList<>());
        deckToCreate.setUserProfile(currentUserProfile);
        currentUserProfile.getDecks().add(deckToCreate);
        deckRepository.save(deckToCreate);
        LOG.debug("Deck with id={}, name='{}' persisted successfully",deckToCreate.getId(), deckToCreate.getName());
        return DeckOverview.fromEntity(deckToCreate);
    }

    @Override
    public List<DeckView> getAllCurrentUserProfileDecks() {
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        LOG.debug("Fetching all decks for UserProfile with id={}", profile.getId());
        return deckRepository.findByUserProfile(profile);
    }

    @Override
    public DeckOverview getDeckById(long deckId) {
        Deck found = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(found.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }
        return DeckOverview.fromEntity(found);
    }

    @Transactional
    @Override
    public void updateDeck(long deckId, UpdateDeckRequest body) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        deckRepository.updateById(deckId, body.name());
    }

    @Transactional
    @Override
    public void deleteDeck(long deckId) {
        Deck foundDeck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(foundDeck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }
        deckRepository.delete(foundDeck);
    }

    @Override
    public DeckDetails extractDeckDetails(long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        int totalCardNum = deck.getFlashcards().size();
        List<WordFlashcardView> readyForView = wordFlashcardService.fetchAllReadyForViewCardsByDeck(deckId);
        int readyForViewNum = readyForView.size();
        return new DeckDetails(deck.getId(), deck.getName(), totalCardNum, readyForViewNum);
    }
}
