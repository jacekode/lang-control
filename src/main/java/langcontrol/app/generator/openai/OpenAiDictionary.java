package langcontrol.app.generator.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.PartOfSpeech;
import langcontrol.app.generator.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OpenAiDictionary implements Dictionary {

    private final OpenAiService service;

    @Autowired
    public OpenAiDictionary(OpenAiService service) {
        this.service = service;
    }

    @Override
    public List<String> getTranslationsList(String wordOrPhraseToTranslate, LanguageCode translateFrom,
                                            LanguageCode translateTo, PartOfSpeech partOfSpeech) {
        if (translateFrom == translateTo) {
            throw new IllegalArgumentException("Languages to translate from and to cannot be the same.");
        }
        final double temperature = 0.25;
        final int maxTokens = 40;

        String langNameFrom = translateFrom.getFullLanguageName();
        String langNameTo = translateTo.getFullLanguageName();
        String posAsText = partOfSpeech.getStringValue();

        String userMsgContent = String.format("Give me a comma-separated list of %s translations of the following %s %s: \"%s\". " +
                "Don't write any words in %s but only %s translations.",
                langNameTo, langNameFrom, posAsText, wordOrPhraseToTranslate, langNameFrom, langNameTo);
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userMsgContent);
        List<ChatMessage> messageList = new ArrayList<>();
        messageList.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messageList)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
        ChatCompletionResult result = service.createChatCompletion(request);
        String commaSeparatedTranslations = result.getChoices().get(0).getMessage().getContent();
        List<String> translations = Arrays.stream(commaSeparatedTranslations.split("\\s*,\\s*"))
                .distinct()
                .toList();
        return translations;
    }
}
