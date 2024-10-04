package dev.jlynx.langcontrol.deck.dto;

import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.lang.LanguageCode;

public record DeckOverview(long id, String name, LanguageCode targetLang, LanguageCode sourceLang) {

    public static DeckOverview fromEntity(Deck entity) {
        return new DeckOverview(
                entity.getId(),
                entity.getName(),
                entity.getTargetLang(),
                entity.getSourceLang()
        );
    }
}
