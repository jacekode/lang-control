package dev.jlynx.langcontrol.deck.dto;

import dev.jlynx.langcontrol.lang.LanguageCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDeckRequest(
        @NotBlank
        @Size(max = 30)
        String name,

        @NotNull
        LanguageCode targetLang,

        @NotNull
        LanguageCode sourceLang
) {}
