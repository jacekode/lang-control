package langcontrol.app.generator.deepl.client;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DeeplTranslationResponseBody {

    private List<DeeplTranslation> translations;
}
