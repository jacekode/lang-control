package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.deck.DeckRepository;
import dev.jlynx.langcontrol.exception.AccessForbiddenException;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.flashcard.dto.*;
import dev.jlynx.langcontrol.generator.Dictionary;
import dev.jlynx.langcontrol.spacedrepetition.RatingType;
import dev.jlynx.langcontrol.spacedrepetition.SpacedRepetitionAlgorithm;
import dev.jlynx.langcontrol.spacedrepetition.SpacedRepetitionSortBy;
import dev.jlynx.langcontrol.url.SortOrder;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordFlashcardServiceImpl implements WordFlashcardService {

    private static final Logger LOG = LoggerFactory.getLogger(WordFlashcardServiceImpl.class);

    private final WordFlashcardRepository wordFlashcardRepository;
    private final DeckRepository deckRepository;
    private final Dictionary dictionary;
    private final SpacedRepetitionAlgorithm algorithm;
    private final UserProfileService userProfileService;

    @Autowired
    public WordFlashcardServiceImpl(WordFlashcardRepository wordFlashcardRepository,
                                    DeckRepository deckRepository,
                                    Dictionary dictionary,
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
    public WordFlashcardOverviewResponse createNewFlashcard(CreateWordFlashcardRequest body) {
        UserProfile currentProfile = userProfileService.retrieveCurrentProfileEntity();
        Deck deck = deckRepository.findById(body.deckId()).orElseThrow(AssetNotFoundException::new);
        if (!Objects.equals(deck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessForbiddenException();
        }
        WordFlashcard flashcardToCreate = WordFlashcard.inInitialLearnMode()
                .withDeck(deck)
                .withSourceLang(deck.getSourceLang())
                .withTargetLang(deck.getTargetLang())
                .withTranslatedWord(body.translatedWord())
                .withTargetWord(body.targetWord())
                .withDynamicExamples(body.dynamicExamples())
                .withPos(body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech())
                .withTargetExample(body.targetExample() == null ? "" : body.targetExample())
                .withTranslatedExample(body.translatedExample() == null ? "" : body.translatedExample())
                .build();
        deck.addFlashcard(flashcardToCreate);
        wordFlashcardRepository.save(flashcardToCreate);
        LOG.debug("Created new WordFlashcard with id={}", flashcardToCreate.getId());
        return WordFlashcardOverviewResponse.fromEntity(flashcardToCreate);
    }

    @Transactional
    @Override
    public WordFlashcardOverviewResponse createNewFlashcardZenMode(CreateWordFlashcardZenModeRequest body) {
        UserProfile currentProfile = userProfileService.retrieveCurrentProfileEntity();
        Deck retrievedDeck = deckRepository.findById(body.deckId()).orElseThrow(AssetNotFoundException::new);
        if (!Objects.equals(retrievedDeck.getUserProfile().getId(), currentProfile.getId())) {
            throw new AccessForbiddenException();
        }

        String targetSideTranslations = dictionary.getTranslationsList(
                body.targetWord(),
                retrievedDeck.getTargetLang(),
                retrievedDeck.getSourceLang(),
                body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech()
        ).stream()
//                .distinct()
                // todo: is the limit here necessary?
                .limit(3)
                .collect(Collectors.joining("; "));

        WordFlashcard flashcardToCreate = WordFlashcard.inInitialLearnMode()
                .withDeck(retrievedDeck)
                .withSourceLang(retrievedDeck.getSourceLang())
                .withTargetLang(retrievedDeck.getTargetLang())
                .withTranslatedWord(targetSideTranslations)
                .withTargetWord(body.targetWord())
                .withDynamicExamples(true)
                .withPos(body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech())
                .withTargetExample("")
                .withTranslatedExample("")
                .build();
        retrievedDeck.addFlashcard(flashcardToCreate);
        wordFlashcardRepository.save(flashcardToCreate);
        return WordFlashcardOverviewResponse.fromEntity(flashcardToCreate);
    }

    @Override
    public WordFlashcard getCardById(long id) {
        WordFlashcard found = wordFlashcardRepository.findById(id)
                .orElseThrow(AssetNotFoundException::new);

        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(found.getDeck().getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access this flashcard.");
        }
        return found;
    }

    @Override
    public WordFlashcardOverviewResponse getCardOverviewById(long id) {
        WordFlashcard card = this.getCardById(id);
        return WordFlashcardOverviewResponse.fromEntity(card);
    }

    @Override
    public IntervalForecastResponse getIntervalForecasts(long cardId) {
        WordFlashcard card = this.getCardById(cardId);

        IntervalForecastResponse forecast = new IntervalForecastResponse();
        forecast.setForLearnKnow(algorithm.calculateNextInterval(card, RatingType.LEARN_KNOW));
        forecast.setForLearnDontKnow(algorithm.calculateNextInterval(card, RatingType.LEARN_DONT_KNOW));
        forecast.setForReviewRemember(algorithm.calculateNextInterval(card, RatingType.REVIEW_REMEMBER));
        forecast.setForReviewPartially(algorithm.calculateNextInterval(card, RatingType.REVIEW_PARTIALLY));
        forecast.setForReviewForgot(algorithm.calculateNextInterval(card, RatingType.REVIEW_FORGOT));
        return forecast;
    }

    @Override
    public void updateFlashcard(long cardId, UpdateWordFlashcardRequest body) {
        WordFlashcard card = wordFlashcardRepository.findById(cardId)
                .orElseThrow(AssetNotFoundException::new);

        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(card.getDeck().getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access card of id=" + cardId);
        }

        wordFlashcardRepository.updateById(
                cardId,
                body.targetWord(),
                body.translatedWord(),
                body.partOfSpeech() == null ? PartOfSpeech.OTHER : body.partOfSpeech(),
                body.dynamicExamples(),
                body.targetExample() == null ? "" : body.targetExample(),
                body.translatedExample() == null ? "" : body.translatedExample()
        );
    }

    @Transactional
    @Override
    public void deleteFlashcard(long flashcardId) {
        WordFlashcard foundCard = this.getCardById(flashcardId);
        wordFlashcardRepository.delete(foundCard);
    }

    @Override
    public List<WordFlashcardView> fetchAllReadyForViewCardsByDeck(long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);

        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        List<WordFlashcardView> flashcards = wordFlashcardRepository.findReadyForViewByDeck(
                deckId,
                LocalDateTime.now(Clock.systemUTC()),
                Sort.unsorted(),
                Limit.unlimited()
        );
        return flashcards;
    }

    @Override
    public List<WordFlashcardView> fetchReadyForView(long deckId, int limit, SpacedRepetitionSortBy sortBy, SortOrder order) {
        if (limit < 1) {
            throw new IllegalArgumentException("Result set limit cannot be less than one.");
        }

        Deck deck = deckRepository.findById(deckId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId);
        }

        Sort sort = Sort.by(order == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.getFieldName());
        List<WordFlashcardView> readyForView = wordFlashcardRepository.findReadyForViewByDeck(
                deckId,
                LocalDateTime.now(Clock.systemUTC()),
                sort,
                Limit.of(limit)
        );
        return new ArrayList<>(readyForView);
    }

    /**
     * {@inheritDoc}
     *
     * @throws AssetNotFoundException if deck with {@code deckId} does not exist
     * @throws AccessForbiddenException if deck with {@code deckId} does not belong to the current {@link UserProfile}
     */
    @Override
    public Page<WordFlashcardView> getFlashcards(Optional<Long> deckId, int pageNum, int pageSize, FlashcardSortBy sortBy, SortOrder order) {
        UserProfile profile = userProfileService.retrieveCurrentProfileEntity();

        Sort sort;
        if (order == SortOrder.ASC) {
            sort = sortBy == null ? Sort.unsorted() : Sort.by(sortBy.getFieldName()).ascending();
        } else {
            sort = sortBy == null ? Sort.unsorted() : Sort.by(sortBy.getFieldName()).descending();
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        if (deckId.isEmpty()) {
            return wordFlashcardRepository.findAllFlashcardViews(profile.getId(), pageable);
        }

        Deck deck = deckRepository.findById(deckId.get()).orElseThrow(() -> {
            LOG.debug("Deck with id={} doesn't exist. AssetNotFoundException was thrown", deckId.get());
            return new AssetNotFoundException(String.format("The deck with id=%d doesn't exist.", deckId.get()));
        });
        if (!Objects.equals(deck.getUserProfile().getId(), profile.getId())) {
            LOG.debug("Tried to access deck data with id={} which doesn't belong to the current user", deckId.get());
            throw new AccessForbiddenException("You don't have permission to access deck of id=" + deckId.get());
        }
        return wordFlashcardRepository.findByDeck_Id(deckId.get(), pageable);
    }
}
