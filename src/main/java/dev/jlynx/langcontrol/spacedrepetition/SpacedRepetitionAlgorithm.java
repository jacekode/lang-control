package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.Flashcard;

public interface SpacedRepetitionAlgorithm {

    void apply(Flashcard card, RatingType rating);

    /**
     * Calculates the length of the interval in minutes that would be applied to the card if it was given the specified
     * rating. This method does not apply the algorithm to the card, so it doesn't modify the Flashcard object's
     * state - it only returns the number of minutes.
     *
     * @param card A Flashcard object to make the calculation for
     * @param rating The rating that would be applied to the card
     * @return the number of minutes constituting the next interval or null if the wrong RatingType is applied
     */
    Long calculateNextInterval(Flashcard card, RatingType rating);
}
