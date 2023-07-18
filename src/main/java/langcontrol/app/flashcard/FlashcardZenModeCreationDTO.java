package langcontrol.app.flashcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FlashcardZenModeCreationDTO {

    @NotBlank
    @Size(max = 80)
    private String back;
    private PartOfSpeech partOfSpeech;
}
