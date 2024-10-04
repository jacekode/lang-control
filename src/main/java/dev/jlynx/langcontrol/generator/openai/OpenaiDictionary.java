package dev.jlynx.langcontrol.generator.openai;

import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.generator.Dictionary;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest.ChatMessage;
import dev.jlynx.langcontrol.generator.openai.dto.ChatMessageRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OpenaiDictionary implements Dictionary {

    private static final Logger LOG = LoggerFactory.getLogger(OpenaiDictionary.class);

    private static final String MODEL = "gpt-4o-mini";
    private static final double TEMPERATURE = 0.25;
    private static final int MAX_TOKENS = 40;

    private final OpenaiClient client;

    @Autowired
    public OpenaiDictionary(OpenaiClient client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if {@code translateFrom} and {@code translateTo} enums are equal
     */
    @Override
    public List<String> getTranslationsList(String sourceText, LanguageCode translateFrom,
                                            LanguageCode translateTo, PartOfSpeech partOfSpeech) {
        if (translateFrom == translateTo) {
            throw new IllegalArgumentException("Languages to translate from and to cannot be the same.");
        }

        String langTo = translateTo.getFullLanguageName();
        String langFrom = translateFrom.getFullLanguageName();
        String pos = partOfSpeech.getStringValue();

        String systemMsgContent = "You're a creative and helpful assistant";
        String userMsgContent = String.format(
                "Give me a comma-separated list of %s translations of the following %s %s: \"%s\". " +
                "Don't write any words in %s but only the %s translations.",
                langTo, langFrom, pos, sourceText, langFrom, langTo
        );

        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM, systemMsgContent);
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER, userMsgContent);
//        List<ChatMessage> messages = List.of(systemMessage, userMessage);
        List<ChatMessage> messages = List.of(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .temperature(TEMPERATURE)
                .maxCompletionTokens(MAX_TOKENS)
                .build();

        var response = client.getChatCompletion(request);
        LOG.debug("Status code {} returned.", response.getStatusCode());
        String responseMsgContent = response.getBody().choices().get(0).message().content();
        return Arrays.stream(responseMsgContent.split("\\s*,\\s*"))
                .distinct()
                .toList();
    }
}
