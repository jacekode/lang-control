package dev.jlynx.langcontrol.flashcard.dto;

import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWordFlashcardZenModeRequest(
        @NotBlank
        @Size(max = 80)
        String targetWord,

        PartOfSpeech partOfSpeech,

        @Min(1)
        long deckId
) {}
