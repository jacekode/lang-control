package langcontrol.app.flashcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import langcontrol.app.usersettings.UserSettings;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FlashcardCreationDTO {

    @NotBlank
    @Size(max = 80)
    private String front;

    @NotBlank
    @Size(max = 80)
    private String back;
    private PartOfSpeech partOfSpeech;
    private boolean dynamicExamples;
    private String targetExample;
    private String translatedExample;

    public static FlashcardCreationDTO withUserSettings(UserSettings userSettings) {
        FlashcardCreationDTO dto = new FlashcardCreationDTO();
        dto.setDynamicExamples(userSettings.isDynamicSentencesOnByDefault());
        return dto;
    }
}
