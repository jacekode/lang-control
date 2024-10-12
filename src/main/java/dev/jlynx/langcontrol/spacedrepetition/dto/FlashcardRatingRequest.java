package dev.jlynx.langcontrol.spacedrepetition.dto;

import dev.jlynx.langcontrol.spacedrepetition.RatingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FlashcardRatingRequest(
        @Min(1)
        long cardId,
        @NotNull
        RatingType rating
) {}
