package langcontrol.app.flashcard;

import jakarta.persistence.*;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.LanguageCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
@Entity
@DiscriminatorValue("word")
public class WordFlashcard extends Flashcard {

    /**
     * A word or phrase in the source language (which the user knows).
     */
    @Column(name = "translated_word", nullable = false)
    private String translatedWord;

    /**
     * A word or phrase in the target language (which the user is learning).
     */
    @Column(name = "target_word", nullable = false)
    private String targetWord;

    @Enumerated(EnumType.STRING)
    @Column(name = "pos")
    private PartOfSpeech pos;

    @Column(name = "dynamic_examples", nullable = false)
    private boolean dynamicExamples;

    /**
     * An example sentence in the target language (which the user is learning).
     */
    @Column(name = "target_example")
    private String targetExample;

    /**
     * An example sentence in the source language (which the user knows).
     */
    @Column(name = "translated_example")
    private String translatedExample;

    protected WordFlashcard() { }

    private WordFlashcard(Deck deck, LanguageCode sourceLang, LanguageCode targetLang,
                          String translatedWord, String targetWord, PartOfSpeech pos, boolean dynamicExamples,
                          String targetExample, String translatedExample, boolean initialReviewMode) {
        super(deck, sourceLang, targetLang, initialReviewMode);
        this.translatedWord = translatedWord;
        this.targetWord = targetWord;
        this.pos = pos;
        this.dynamicExamples = dynamicExamples;
        this.targetExample = targetExample;
        this.translatedExample = translatedExample;
    }

    public static WordFlashcard.Builder inInitialLearnMode() {
        return new Builder(false);
    }

    public static WordFlashcard.Builder inInitialReviewMode() {
        return new Builder(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordFlashcard flashcard = (WordFlashcard) o;
        return Objects.equals(id, flashcard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public static class Builder {

        private final boolean initialReviewMode;
        private Deck deck;
        private LanguageCode sourceLang;
        private LanguageCode targetLang;
        private String front;
        private String back;
        private PartOfSpeech pos;
        private boolean dynamicExamples;
        private String example;
        private String translatedExample;

        public Builder(boolean initialReviewMode) {
            this.initialReviewMode = initialReviewMode;
            this.deck = null;
            this.sourceLang = null;
            this.targetLang = null;
            this.front = "";
            this.back = "";
            this.pos = null;
            this.dynamicExamples = false;
            this.example = "";
            this.translatedExample = "";
        }

        public Builder withDeck(Deck deck) {
            this.deck = deck;
            return this;
        }

        /**
         * Sets the language that the user knows.
         * @param sourceLanguage The language to set
         * @return This builder instance with the corresponding attribute set
         */
        public Builder withSourceLang(LanguageCode sourceLanguage) {
            this.sourceLang = sourceLanguage;
            return this;
        }

        /**
         * Sets the language that the user is learning.
         * @param targetLanguage The language to set
         * @return This builder instance with the corresponding attribute set
         */
        public Builder withTargetLang(LanguageCode targetLanguage) {
            this.targetLang = targetLanguage;
            return this;
        }

        /**
         * Sets the content of the card in the source language (which the user knows).
         * @param front The word or phrase to set
         * @return This builder instance with the corresponding attribute set
         */
        public Builder withFront(String front) {
            this.front = front;
            return this;
        }

        /**
         * Sets the content of the card in the target language (which the user is learning).
         * @param back The word or phrase to set
         * @return This builder instance with the corresponding attribute set
         */
        public Builder withBack(String back) {
            this.back = back;
            return this;
        }

        public Builder withPos(PartOfSpeech partOfSpeech) {
            this.pos = partOfSpeech;
            return this;
        }

        public Builder withDynamicExamples(boolean dynamicExamples) {
            this.dynamicExamples = dynamicExamples;
            return this;
        }

        public Builder withExample(String example) {
            this.example = example;
            return this;
        }

        public Builder withTranslatedExample(String translatedExample) {
            this.translatedExample = translatedExample;
            return this;
        }

        /**
         * Creates a WordFlashcard instance with the attributes previously specified in the builder.
         * @return A WordFlashcard instance
         */
        public WordFlashcard build() {
                return new WordFlashcard(deck, sourceLang, targetLang,
                        front, back, pos, dynamicExamples, example,
                        translatedExample, initialReviewMode);
        }
    }
}