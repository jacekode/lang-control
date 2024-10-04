package dev.jlynx.langcontrol.generator.deepl;

import com.deepl.api.*;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.generator.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeeplTranslator implements Translator {

    private final com.deepl.api.Translator client;

    @Autowired
    public DeeplTranslator(com.deepl.api.Translator client) {
        this.client = client;
    }

    @Override
    public String translate(String textToTranslate, LanguageCode translateTo) {
        String translateToCode = addRegionSuffix(translateTo.getCode());
        TextResult response;
        try {
            response = client.translateText(
                    textToTranslate,
                    null,
                    translateToCode,
                    new TextTranslationOptions().setSentenceSplittingMode(SentenceSplittingMode.Off)
            );
        } catch (InterruptedException | DeepLException e) {
            throw new RuntimeException(e);
        }
        return response.getText();
    }

    @Override
    public String translate(String textToTranslate, LanguageCode translateTo, LanguageCode translateFrom) {
        String translateToCode = addRegionSuffix(translateTo.getCode());
        TextResult response;
        try {
            response = client.translateText(
                    textToTranslate,
                    translateFrom == null ? null : translateFrom.getCode(),
                    translateToCode,
                    new TextTranslationOptions().setSentenceSplittingMode(SentenceSplittingMode.Off)
            );
        } catch (InterruptedException | DeepLException e) {
            throw new RuntimeException(e);
        }
        return response.getText();
    }

    /**
     * Adds to regional variant suffix to supported translation target language codes.
     * E.g. changes "en" to "en-GB" for British English or "pt" to "pt-PT" for European Portuguese.
     *
     * @param isoCode ISO6391
     * @return An updated language code. If the language code doesn't support regional variants, it'll be left unchanged.
     */
    private String addRegionSuffix(String isoCode) {
        if (isoCode.equals("en")) {
            return "en-GB";
        }
        if (isoCode.equals("pt")) {
            return "pt-PT";
        }
        return isoCode;
    }
}
