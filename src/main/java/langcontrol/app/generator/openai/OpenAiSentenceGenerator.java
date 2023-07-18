package langcontrol.app.generator.openai;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.PartOfSpeech;
import langcontrol.app.generator.SentenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiSentenceGenerator implements SentenceGenerator {

    private final OpenAiService service;

    @Autowired
    public OpenAiSentenceGenerator(OpenAiService openAiService) {
        this.service = openAiService;
    }

    @Override
    public List<String> generate(String keyword, LanguageCode keywordLanguage, PartOfSpeech keywordPos,
                                 int numberOfSentences) {
        final double temperature = 1.3; // set no higher than 1.3
        final int maxTokens = 40;

        String langName = keywordLanguage.getFullLanguageName();
        String userMsgContent = String.format("Create an example sentence in %s which contains the following %s %s: \"%s\". " +
                        "However, do not generate any translations of the sentence.",
                langName, langName, keywordPos.getStringValue(), keyword);

        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "You are a creative AI assistant.");
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userMsgContent);
        List<ChatMessage> messageList = new ArrayList<>();
//        messageList.add(systemMessage);
        messageList.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messageList)
                .n(numberOfSentences)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
        ChatCompletionResult result = service.createChatCompletion(request);
        List<String> answerList = result.getChoices().stream()
                .map(c -> c.getMessage().getContent())
                .toList();
        return answerList;
    }
}
