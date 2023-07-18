package langcontrol.app.flashcard;

import jakarta.persistence.*;
import langcontrol.app.deck.Deck;
import langcontrol.app.spaced_repetition.SpacedRepetitionItem;
import langcontrol.app.deck.LanguageCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter
@Entity
@Table(name = "flashcard")
public class Flashcard extends SpacedRepetitionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_flashcard_deck"))
    private Deck deck;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_language", nullable = false)
    private LanguageCode sourceLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_language", nullable = false)
    private LanguageCode targetLanguage;

    @Column(nullable = false)
    private String front;

    @Column(nullable = false)
    private String back;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    @Column(name = "dynamic_examples")
    private boolean dynamicExamples;

    @Column
    private String example;

    @Column(name = "translated_example")
    private String translatedExample;


    @Column(name = "creation_date_time_in_utc", nullable = false)
    private LocalDateTime creationDateTimeInUTC;


    public Flashcard() {
        super(false);
    }

    private Flashcard(Deck deck, LanguageCode sourceLanguage, LanguageCode targetLanguage,
                      String front, String back, PartOfSpeech partOfSpeech, boolean dynamicExamples,
                      String example, String translatedExample, boolean initialReviewMode) {
        super(initialReviewMode);
        this.id = null;
        this.deck = deck;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.front = front;
        this.back = back;
        this.partOfSpeech = partOfSpeech;
        this.dynamicExamples = dynamicExamples;
        this.example = example;
        this.translatedExample = translatedExample;
        this.creationDateTimeInUTC = LocalDateTime.now(Clock.systemUTC());
    }

    public static Flashcard.Builder inInitialLearnModeState() {
        return new Builder(false);
    }

    public static Flashcard.Builder inInitialReviewModeState() {
        return new Builder(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flashcard flashcard = (Flashcard) o;
        return Objects.equals(id, flashcard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public static class Builder {

        private final boolean initialReviewMode;
        private Deck deck;
        private LanguageCode sourceLanguage;
        private LanguageCode targetLanguage;
        private String front;
        private String back;
        private PartOfSpeech partOfSpeech;
        private boolean dynamicExamples;
        private String example;
        private String translatedExample;

        public Builder(boolean initialReviewMode) {
            this.initialReviewMode = initialReviewMode;
            this.deck = null;
            this.sourceLanguage = null;
            this.targetLanguage = null;
            this.front = "";
            this.back = "";
            this.partOfSpeech = null;
            this.dynamicExamples = false;
            this.example = "";
            this.translatedExample = "";
        }

        public Builder deck(Deck deck) {
            this.deck = deck;
            return this;
        }

        public Builder sourceLanguage(LanguageCode sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
            return this;
        }

        public Builder targetLanguage(LanguageCode targetLanguage) {
            this.targetLanguage = targetLanguage;
            return this;
        }

        public Builder front(String front) {
            this.front = front;
            return this;
        }

        public Builder back(String back) {
            this.back = back;
            return this;
        }

        public Builder partOfSpeech(PartOfSpeech partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
            return this;
        }

        public Builder dynamicExamples(boolean dynamicExamples) {
            this.dynamicExamples = dynamicExamples;
            return this;
        }

        public Builder example(String example) {
            this.example = example;
            return this;
        }

        public Builder translatedExample(String translatedExample) {
            this.translatedExample = translatedExample;
            return this;
        }

        public Flashcard build() {
                return new Flashcard(deck, sourceLanguage, targetLanguage,
                        front, back, partOfSpeech, dynamicExamples, example,
                        translatedExample, initialReviewMode);
        }
    }
}