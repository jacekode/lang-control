package dev.jlynx.langcontrol.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDeckRequest(@NotBlank @Size(max = 30) String name) {
}
