package langcontrol.app.deck;

import jakarta.persistence.*;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.user_profile.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deck")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_profile_id", foreignKey = @ForeignKey(name = "fk_deck_user_profile"))
    private UserProfile userProfile;

    @Column
    private LanguageCode targetLanguage;

    @Column
    private LanguageCode sourceLanguage;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flashcard> flashcards;

    public Deck(CreateDeckDTO createDeckDTO) {
        this.id = null;
        this.name = createDeckDTO.getName();
        this.targetLanguage = createDeckDTO.getTargetLanguage();
        this.sourceLanguage = createDeckDTO.getSourceLanguage();
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
