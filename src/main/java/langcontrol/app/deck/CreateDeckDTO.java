package langcontrol.app.deck;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDeckDTO {

    @NotNull @NotBlank
    @Size(max = 30)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LanguageCode targetLanguage;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LanguageCode sourceLanguage;
}
