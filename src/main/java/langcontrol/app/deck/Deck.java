package langcontrol.app.deck;

import jakarta.persistence.*;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.userprofile.UserProfile;
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

    @ManyToOne
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

    public Deck(CreateDeckDTO createDeckDTO) {
        this.id = null;
        this.name = createDeckDTO.getName();
        this.targetLang = createDeckDTO.getTargetLanguage();
        this.sourceLang = createDeckDTO.getSourceLanguage();
        this.flashcards = new ArrayList<>();
        this.userProfile = null;
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
