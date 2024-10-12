package dev.jlynx.langcontrol.flashcard.dto;

import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the body of a {@link WordFlashcard} creation request.
 *
 * @param translatedWord a word or phrase in the source language (which the user knows); max 80 characters
 * @param targetWord a word or phrase in the target language (which the user is learning); max 80 characters
 * @param partOfSpeech the grammatical part of speech of the {@code targetWord}
 * @param dynamicExamples sets whether the {@link WordFlashcard#isDynamicExamples()} setting should be on or off
 * @param targetExample an example sentence in the target language (which the user is learning); max 300 characters
 * @param translatedExample an example sentence in the source language (which the user knows); max 300 characters
 * @param deckId the id of the deck that the card should be added to
 */
public record CreateWordFlashcardRequest(
        @NotBlank
        @Size(max = 80)
        String translatedWord,

        @NotBlank
        @Size(max = 80)
        String targetWord,

        PartOfSpeech partOfSpeech,
        boolean dynamicExamples,

        @NotNull
        @Size(max = 300)
        String translatedExample,

        @NotNull
        @Size(max = 300)
        String targetExample,

        @Min(1)
        long deckId
) {}
