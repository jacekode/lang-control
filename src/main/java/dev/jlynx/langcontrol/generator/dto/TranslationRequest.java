package dev.jlynx.langcontrol.generator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.langcontrol.generator.Translator;
import dev.jlynx.langcontrol.lang.LanguageCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a translation request body indirectly consumed by {@link Translator} implementations.
 *
 * @param text text to be translated; maximum 300 characters long
 * @param translateTo the {@link LanguageCode} of the desired translation language
 * @param translateFrom the {@link LanguageCode} of the source text
 */
public record TranslationRequest(
        @NotBlank @Size(max = 300) String text,
        @JsonProperty("to") LanguageCode translateTo,
        @JsonProperty("from") LanguageCode translateFrom
) {}
