package dev.jlynx.langcontrol.generator.openai;

import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.generator.SentenceGenerator;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest.ChatMessage;
import dev.jlynx.langcontrol.generator.openai.dto.ChatMessageRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenaiSentenceGenerator implements SentenceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OpenaiSentenceGenerator.class);

    private static final String MODEL = "gpt-4o-mini";
    private static final double TEMPERATURE = 1.3;
    private static final int MAX_TOKENS = 40;

    private final OpenaiClient client;

    @Autowired
    public OpenaiSentenceGenerator(OpenaiClient client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException when the {@code numberOfSentences} argument is less than 1
     */
    @Override
    public List<String> generate(String keyword, LanguageCode keywordLang, PartOfSpeech keywordPos,
                                 int numberOfSentences) {
        if (numberOfSentences < 1) {
            LOG.debug("Invalid argument value of '{}' for numberOfSentences", numberOfSentences);
            throw new IllegalArgumentException("The number of sentences to generate cannot be less than one.");
        }

        String lang = keywordLang.getFullLanguageName();

        String systemMsgContent = "You're a creative and helpful assistant";
        String userMsgContent = String.format(
                "Create an example sentence in %s which contains the following %s %s: \"%s\". " +
                        "However, do not generate any translations of the sentence.",
                lang, lang, keywordPos.getStringValue(), keyword
        );

        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM, systemMsgContent);
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER, userMsgContent);
//        List<ChatMessage> messages = List.of(systemMessage, userMessage);
        List<ChatMessage> messages = List.of(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .n(numberOfSentences)
                .temperature(TEMPERATURE)
                .maxCompletionTokens(MAX_TOKENS)
                .build();

        var response = client.getChatCompletion(request);
        LOG.debug("Status code {} returned. Number of generated sentences: {}",
                response.getStatusCode(), response.getBody().choices().size());
        return response.getBody().choices().stream()
                .map(choice -> choice.message().content())
                .toList();
    }
}
