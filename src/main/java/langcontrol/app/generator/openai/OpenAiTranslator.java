package langcontrol.app.generator.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.exception.OpenAiTranslationErrorException;
import langcontrol.app.generator.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiTranslator implements Translator {

    private final Logger logger = LoggerFactory.getLogger(OpenAiTranslator.class);
    private final OpenAiService service;

    @Autowired
    public OpenAiTranslator(OpenAiService service) {
        this.service = service;
    }

    @Override
    public String translate(String textToTranslate, LanguageCode translateTo) throws OpenAiTranslationErrorException {
        final double temperature = 1.0;
        String errorCode = OpenAiCustomErrorCode.SAME_LANGUAGES.getValue();

        String langName = translateTo.getFullLanguageName();
        String userMsgContent = String.format("Translate the following text to %s: %s. However, " +
                        "if it's already in %s, write the text \"%s\" instead.",
                langName, textToTranslate, langName, errorCode);
        logger.info("OpenAI user message: {}", userMsgContent);

        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userMsgContent);
        List<ChatMessage> messageList = new ArrayList<>();
        messageList.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messageList)
                .temperature(temperature)
                .build();
        ChatCompletionResult result = service.createChatCompletion(request);

        String receivedTranslation = result.getChoices().get(0).getMessage().getContent();

        if (receivedTranslation.contains(errorCode)) {
            logger.info("Received error code {} from the OpenAI API", errorCode);
            throw new OpenAiTranslationErrorException(
                    "It was not possible to get the translation from the OpenAI API.");
        }
        return result.getChoices().get(0).getMessage().getContent();
    }
}
