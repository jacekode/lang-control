package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckRepository;
import langcontrol.app.exception.AccessNotAllowedException;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.generator.Dictionary;
import langcontrol.app.spacedrepetition.RatingType;
import langcontrol.app.spacedrepetition.SpacedRepetitionAlgorithm;
import langcontrol.app.userprofile.UserProfile;
import langcontrol.app.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordFlashcardServiceImpl implements WordFlashcardService {

    private final WordFlashcardRepository wordFlashcardRepository;
    private final DeckRepository deckRepository;
    private final Dictionary dictionary;
    private final SpacedRepetitionAlgorithm algorithm;
    private final UserProfileService userProfileService;

    @Autowired
    public WordFlashcardServiceImpl(WordFlashcardRepository wordFlashcardRepository,
                                    DeckRepository deckRepository,
                                    @Qualifier("openAiDictionary") Dictionary dictionary,
                                    SpacedRepetitionAlgorithm algorithm,
                                    UserProfileService userProfileService) {
        this.wordFlashcardRepository = wordFlashcardRepository;
        this.deckRepository = deckRepository;
        this.dictionary = dictionary;
        this.algorithm = algorithm;
        this.userProfileService = userProfileService;
    }


    @Transactional
    @Override
    public void createNewFlashcard(long deckId, FlashcardCreationDTO dto) {
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        Deck retrievedDeck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(retrievedDeck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessNotAllowedException();
        }

        WordFlashcard flashcardToCreate = WordFlashcard.inInitialLearnMode()
                .withDeck(retrievedDeck)
                .withSourceLang(retrievedDeck.getSourceLang())
                .withTargetLang(retrievedDeck.getTargetLang())
                .withFront(dto.getFront())
                .withBack(dto.getBack())
                .withDynamicExamples(dto.isDynamicExamples())
                .withPos(dto.getPartOfSpeech() == null ? PartOfSpeech.OTHER : dto.getPartOfSpeech())
                .withExample(dto.getTargetExample())
                .withTranslatedExample(dto.getTranslatedExample())
                .build();
        retrievedDeck.addFlashcard(flashcardToCreate);
    }

    @Transactional
    @Override
    public void createNewFlashcardZenMode(long deckId, FlashcardZenModeCreationDTO dto) {
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        Deck retrievedDeck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(retrievedDeck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessNotAllowedException();
        }

        String targetSideTranslations = dictionary.getTranslationsList(
                dto.getTargetWord(),
                retrievedDeck.getTargetLang(),
                retrievedDeck.getSourceLang(),
                dto.getPartOfSpeech()
        ).stream()
                .distinct()
                .limit(3)
                .collect(Collectors.joining("; "));

        WordFlashcard flashcardToCreate = WordFlashcard.inInitialLearnMode()
                .withDeck(retrievedDeck)
                .withSourceLang(retrievedDeck.getSourceLang())
                .withTargetLang(retrievedDeck.getTargetLang())
                .withFront(targetSideTranslations)
                .withBack(dto.getTargetWord())
                .withDynamicExamples(true)
                .withPos(dto.getPartOfSpeech() == null ? PartOfSpeech.OTHER : dto.getPartOfSpeech())
                .build();
        retrievedDeck.addFlashcard(flashcardToCreate);
    }

    @Transactional
    @Override
    public WordFlashcard getCardById(long id) {
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        WordFlashcard foundCard = wordFlashcardRepository.findById(id)
                .orElseThrow(GeneralNotFoundException::new);

        if (!Objects.equals(foundCard.getDeck().getUserProfile().getId(),
                currentProfile.getId())) {
            throw new AccessNotAllowedException("You don't have permission to access this flashcard.");
        }
        return foundCard;
    }

    @Override
    public IntervalForecastDTO produceReviewTimeForecastsAsText(long flashcardId) {
        WordFlashcard card = this.getCardById(flashcardId);

        IntervalForecastDTO forecast = new IntervalForecastDTO();
        forecast.setForLearnKnow(algorithm.calculateNextInterval(card, RatingType.LEARN_KNOW));
        forecast.setForLearnDontKnow(algorithm.calculateNextInterval(card, RatingType.LEARN_DONT_KNOW));
        forecast.setForReviewRemember(algorithm.calculateNextInterval(card, RatingType.REVIEW_REMEMBER));
        forecast.setForReviewPartially(algorithm.calculateNextInterval(card, RatingType.REVIEW_PARTIALLY));
        forecast.setForReviewForgot(algorithm.calculateNextInterval(card, RatingType.REVIEW_FORGOT));
        return forecast;
    }

    @Transactional
    @Override
    public void deleteFlashcard(long flashcardId) {
        WordFlashcard foundCard = this.getCardById(flashcardId);
        wordFlashcardRepository.delete(foundCard);
    }

    @Override
    public Deque<WordFlashcard> fetchShuffleReadyForView(Long deckId, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Result set limit cannot be less than one.");
        }
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        Deck retrievedDeck = deckRepository.findById(deckId).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(retrievedDeck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessNotAllowedException();
        }
        List<WordFlashcard> flashcards = wordFlashcardRepository.findReadyForReviewFlashcardsByDeck(
                retrievedDeck,
                LocalDateTime.now(Clock.systemUTC()),
                limit
        );
        Collections.shuffle(flashcards);
        return new ArrayDeque<>(flashcards);
    }

    @Override
    public List<WordFlashcard> getAllFlashcardsByDeck(Deck deck) {
        UserProfile currentProfile = userProfileService.retrieveCurrentUserProfile();
        if (!Objects.equals(deck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessNotAllowedException("You don't have permission to view this deck's data.");
        }
        return wordFlashcardRepository.findByDeck(deck);
    }
}
