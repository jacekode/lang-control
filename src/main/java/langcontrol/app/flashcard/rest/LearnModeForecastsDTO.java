package langcontrol.app.flashcard.rest;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LearnModeForecastsDTO {

    private String forPrevious;
    private String forNormal;
    private String forNext;
    private String forToReviewMode;
}
