package dev.jlynx.langcontrol.deck.view;

import dev.jlynx.langcontrol.lang.LanguageCode;

public record DeckView(Long id, String name, LanguageCode targetLang, LanguageCode sourceLang) {
}
