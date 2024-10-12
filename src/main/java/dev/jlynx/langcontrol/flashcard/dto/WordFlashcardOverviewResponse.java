package dev.jlynx.langcontrol.flashcard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.flashcard.WordFlashcard;

import java.time.LocalDateTime;

public record WordFlashcardOverviewResponse(
        long id,
        String targetWord,
        String translatedWord,
        LanguageCode targetLang,
        LanguageCode sourceLang,
        String targetExample,
        String translatedExample,
        PartOfSpeech partOfSpeech,
        boolean dynamicExamples,
        boolean inLearnMode,
        long currentIntervalMinutes,
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
        LocalDateTime createdAt,
        LocalDateTime nextView,
        DeckOverview deck
) {

    public record DeckOverview(long id, String name) {}

    public static WordFlashcardOverviewResponse fromEntity(WordFlashcard entity) {
        return new WordFlashcardOverviewResponse(
                entity.getId(),
                entity.getTargetWord(),
                entity.getTranslatedWord(),
                entity.getTargetLang(),
                entity.getSourceLang(),
                entity.getTargetExample(),
                entity.getTranslatedExample(),
                entity.getPartOfSpeech(),
                entity.isDynamicExamples(),
                entity.isInLearnMode(),
                entity.getCurrentInterval(),
                entity.getCreatedAt(),
                entity.getNextView(),
                new DeckOverview(entity.getDeck().getId(), entity.getDeck().getName())
        );
    }
}
