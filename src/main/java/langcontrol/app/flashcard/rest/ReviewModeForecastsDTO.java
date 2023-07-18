package langcontrol.app.flashcard.rest;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewModeForecastsDTO {

    private String forCannotSolve;
    private String forDifficult;
    private String forNormal;
    private String forEasy;
}
