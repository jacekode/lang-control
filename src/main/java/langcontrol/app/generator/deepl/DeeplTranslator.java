package langcontrol.app.generator.deepl;

import langcontrol.app.deck.LanguageCode;
import langcontrol.app.generator.Translator;
import langcontrol.app.generator.deepl.client.DeeplClient;
import langcontrol.app.generator.deepl.client.DeeplTranslationResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeeplTranslator implements Translator {

    private final DeeplClient client;

    @Autowired
    public DeeplTranslator(DeeplClient client) {
        this.client = client;
    }

    @Override
    public String translate(String textToTranslate, LanguageCode translateTo) {
        DeeplTranslationResponseBody response = client.performTranslateRequest(textToTranslate,
                translateTo.getCode().toUpperCase());
        return response.getTranslations().get(0).getTranslatedText();
    }
}
