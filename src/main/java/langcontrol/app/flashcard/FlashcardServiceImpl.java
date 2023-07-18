package langcontrol.app.flashcard;

import langcontrol.app.account.Account;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckRepository;
import langcontrol.app.exception.AccessNotAllowedException;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.flashcard.rest.FlashcardForecastsDTO;
import langcontrol.app.flashcard.rest.LearnModeForecastsDTO;
import langcontrol.app.flashcard.rest.ReviewModeForecastsDTO;
import langcontrol.app.generator.openai.OpenAiDictionary;
import langcontrol.app.util.PrincipalRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final OpenAiDictionary dictionary;

    @Autowired
    public FlashcardServiceImpl(FlashcardRepository flashcardRepository, DeckRepository deckRepository,
                                @Qualifier("openAiDictionary") OpenAiDictionary dictionary) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.dictionary = dictionary;
    }

    @Transactional
    @Override
    public void deleteFlashcard(long flashcardId) {
        Flashcard foundCard = this.getCardById(flashcardId);
        flashcardRepository.delete(foundCard);
    }

    @Transactional
    @Override
    public void createNewFlashcard(long deckId, FlashcardCreationDTO dto) {
        Deck retrievedDeck = findDeckById(deckId);

        Flashcard flashcardToCreate = Flashcard.inInitialLearnModeState()
                .deck(retrievedDeck)
                .sourceLanguage(retrievedDeck.getSourceLanguage())
                .targetLanguage(retrievedDeck.getTargetLanguage())
                .front(dto.getFront())
                .back(dto.getBack())
                .dynamicExamples(dto.isDynamicExamples())
                .partOfSpeech(dto.getPartOfSpeech() == null ?
                        PartOfSpeech.OTHER : dto.getPartOfSpeech())
                .example(dto.getExample())
                .translatedExample(dto.getTranslatedExample())
                .build();

        retrievedDeck.addFlashcard(flashcardToCreate);
    }

    @Transactional
    @Override
    public void createNewFlashcardZenMode(long deckId, FlashcardZenModeCreationDTO dto) {
        Deck retrievedDeck = findDeckById(deckId);

        String targetSideTranslations = dictionary.getTranslationsList(dto.getBack(),
                        retrievedDeck.getTargetLanguage(),
                        retrievedDeck.getSourceLanguage(),
                        dto.getPartOfSpeech()).stream()
                .distinct()
                .limit(3)
                .collect(Collectors.joining("; "));

        Flashcard flashcardToCreate = Flashcard.inInitialLearnModeState()
                .deck(retrievedDeck)
                .sourceLanguage(retrievedDeck.getSourceLanguage())
                .targetLanguage(retrievedDeck.getTargetLanguage())
                .front(targetSideTranslations)
                .back(dto.getBack())
                .dynamicExamples(true)
                .partOfSpeech(dto.getPartOfSpeech() == null ?
                        PartOfSpeech.OTHER : dto.getPartOfSpeech())
                .build();

        retrievedDeck.addFlashcard(flashcardToCreate);
    }

    @Override
    public Deque<Flashcard> fetchReadyForReviewShuffledWithLimit(Long deckId, String zoneId, int limit) {
        if(!ZoneId.getAvailableZoneIds().contains(zoneId)) {
            throw new IllegalArgumentException("The specified zone id is incorrect.");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Result set limit cannot be less than one.");
        }
        Deck deck = findDeckById(deckId);
        List<Flashcard> flashcards = flashcardRepository.findReadyForReviewFlashcardsByDeck(
                deck,
                LocalDateTime.now(Clock.systemUTC()),
                ZonedDateTime.now(ZoneId.of(zoneId)).toLocalDateTime().toLocalDate(),
                limit
        );
        Collections.shuffle(flashcards);
        return new ArrayDeque<>(flashcards);
    }

    @Override
    public List<Flashcard> getAllFlashcardsByDeck(Deck deck) {
        Account currentAccount = PrincipalRetriever.retrieveAccount();
        if (!Objects.equals(deck.getUserProfile().getId(), currentAccount.getUserProfile().getId())) {
            throw new AccessNotAllowedException("You don't have permission to view this data.");
        }
        return flashcardRepository.findByDeck(deck);
    }

    @Override
    public FlashcardForecastsDTO produceReviewTimeForecastsAsText(long flashcardId) {
        Flashcard flashcard = this.getCardById(flashcardId);

        final String stepOneText = "in 1 min";
        final String stepTwoText = "in 10 min";
        final String stepThreeText = "in 24 h";
        final String initRevModeText = "in 2 days";

        LearnModeForecastsDTO learnForecasts = new LearnModeForecastsDTO();
        ReviewModeForecastsDTO reviewForecasts = new ReviewModeForecastsDTO();
        if (flashcard.isInLearnMode()) {
            switch (flashcard.getLearnModeStep()) {
                case ONE -> {
                    learnForecasts.setForPrevious("now");
                    learnForecasts.setForNormal(stepOneText);
                    learnForecasts.setForNext(stepTwoText);
                    learnForecasts.setForToReviewMode(initRevModeText);
                }
                case TWO -> {
                    learnForecasts.setForPrevious(stepOneText);
                    learnForecasts.setForNormal(stepTwoText);
                    learnForecasts.setForNext(stepThreeText);
                    learnForecasts.setForToReviewMode(initRevModeText);
                }
                case THREE -> {
                    learnForecasts.setForPrevious(stepTwoText);
                    learnForecasts.setForNormal(stepThreeText);
                    learnForecasts.setForNext(initRevModeText);
                    learnForecasts.setForToReviewMode(initRevModeText);
                }
            }
        } else {
            double interval = flashcard.getCurrentIntervalDays();
            double reduceFactor = flashcard.getReduceFactor();
            double increaseFactor = flashcard.getIncreaseFactor();

            reviewForecasts.setForCannotSolve(interval <= 2D ? stepOneText : initRevModeText);

            int intervalDif = BigDecimal.valueOf(interval * reduceFactor)
                    .setScale(0, RoundingMode.HALF_DOWN).intValue();
            reviewForecasts.setForDifficult(String.format("in %d days", intervalDif));

            int intervalNorm = BigDecimal.valueOf(interval * 1.1)
                    .setScale(0, RoundingMode.HALF_DOWN).intValue();
            reviewForecasts.setForNormal(String.format("in %d days", intervalNorm));

            int intervalEasy = BigDecimal.valueOf(interval * increaseFactor)
                    .setScale(0, RoundingMode.HALF_DOWN).intValue();
            reviewForecasts.setForEasy(String.format("in %d days", intervalEasy));
        }

        return new FlashcardForecastsDTO(learnForecasts, reviewForecasts);
    }

    @Override
    public Flashcard getCardById(long id) {
        Account currentAccount = PrincipalRetriever.retrieveAccount();
        Flashcard foundCard = flashcardRepository.findById(id)
                .orElseThrow(GeneralNotFoundException::new);

        if (!Objects.equals(foundCard.getDeck().getUserProfile().getId(),
                currentAccount.getUserProfile().getId())) {
            throw new AccessNotAllowedException("You don't have permission to perform this action.");
        }
        return foundCard;
    }


    @Transactional
    private Deck findDeckById(long id) {
        Account currentAccount = PrincipalRetriever.retrieveAccount();
        Deck foundDeck = deckRepository.findById(id).orElseThrow(GeneralNotFoundException::new);
        if (!Objects.equals(foundDeck.getUserProfile().getId(), currentAccount.getUserProfile().getId())) {
            throw new AccessNotAllowedException("You don't have permission to perform this action.");
        }
        return foundDeck;
    }
}
