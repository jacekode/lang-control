package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.lang.LanguageCode;
import jakarta.persistence.*;
import dev.jlynx.langcontrol.flashcard.Flashcard;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deck")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id", foreignKey = @ForeignKey(name = "fk_deck_user_profile"))
    private UserProfile userProfile;

    /**
     * The language that the user is learning.
     */
    @Column(name = "target_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    private LanguageCode targetLang;

    /**
     * The language that the user knows.
     */
    @Column(name = "source_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    private LanguageCode sourceLang;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flashcard> flashcards;


    private Deck(CreateDeckRequest requestBody) {
        this.id = null;
        this.name = requestBody.name();
        this.targetLang = requestBody.targetLang();
        this.sourceLang = requestBody.sourceLang();
        this.flashcards = new ArrayList<>();
        this.userProfile = null;
    }

    public static Deck fromRequest(CreateDeckRequest requestBody) {
        return new Deck(requestBody);
    }

    public void addFlashcard(Flashcard flashcard) {
        flashcard.setDeck(this);
        this.flashcards.add(flashcard);
    }

    public void removeFlashcard(Flashcard flashcard) {
        flashcard.setDeck(null);
        this.flashcards.remove(flashcard);
    }
}
