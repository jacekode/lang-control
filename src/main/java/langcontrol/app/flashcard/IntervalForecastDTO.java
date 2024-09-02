package langcontrol.app.flashcard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Carries the prediction data for a flashcard's intervals in case a particular RatingType is applied to it.
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntervalForecastDTO {

    /**
     * The interval in minutes that'll be set in case RatingType.LEARN_KNOW is applied.
     */
    private Long forLearnKnow;

    /**
     * The interval in minutes that'll be set in case RatingType.LEARN_DONT_KNOW is applied.
     */
    private Long forLearnDontKnow;

    /**
     * The interval in minutes that'll be set in case RatingType.REVIEW_REMEMBER is applied.
     */
    private Long forReviewRemember;

    /**
     * The interval in minutes that'll be set in case RatingType.REVIEW_PARTIALLY is applied.
     */
    private Long forReviewPartially;

    /**
     * The interval in minutes that'll be set in case RatingType.REVIEW_FORGOT is applied.
     */
    private Long forReviewForgot;
}
