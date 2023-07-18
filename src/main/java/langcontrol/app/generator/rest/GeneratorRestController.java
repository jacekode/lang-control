package langcontrol.app.generator.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import langcontrol.app.exception.ErrorResponseBody;
import langcontrol.app.exception.OpenAiTranslationErrorException;
import langcontrol.app.flashcard.PartOfSpeech;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.generator.Dictionary;
import langcontrol.app.generator.SentenceGenerator;
import langcontrol.app.generator.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class GeneratorRestController {

    private final Logger logger = LoggerFactory.getLogger(GeneratorRestController.class);

    private final Dictionary dictionary;
    private final SentenceGenerator sentenceGenerator;
    private final Translator translator;

    @Autowired
    public GeneratorRestController(@Qualifier("openAiDictionary") Dictionary dictionary,
                                   SentenceGenerator sentenceGenerator,
                                   @Qualifier("deeplTranslator") Translator translator) {
        this.dictionary = dictionary;
        this.sentenceGenerator = sentenceGenerator;
        this.translator = translator;
    }

    @GetMapping(value = "/dictionary", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getWordTranslations(
            @NotBlank @RequestParam("word") String wordToTranslate,
            @NotNull @RequestParam("from") LanguageCode translateFrom,
            @NotNull @RequestParam("to") LanguageCode translateTo,
            @NotNull @RequestParam(value = "pos") PartOfSpeech partOfSpeech) {

        logger.info("getWordTranslations() method was called with the following parameters:" +
                "wordToTranslate={} translateFrom={} translateTo={} partOfSpeech={}",
                wordToTranslate, translateFrom, translateTo, partOfSpeech);

        return dictionary.getTranslationsList(wordToTranslate, translateFrom,
                    translateTo, partOfSpeech);
    }

    @GetMapping(value = "/sentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getGeneratedSentences(@NotBlank @Size(max = 80) @RequestParam("word") String keyword,
                                              @NotNull @RequestParam("lang") LanguageCode keywordLang,
                                              @NotNull @RequestParam(value = "pos") PartOfSpeech keywordPos,
                                              @Max(3) @RequestParam("n") int numOfSentences) {
        List<String> sentences = sentenceGenerator.generate(keyword, keywordLang, keywordPos, numOfSentences);
        return sentences;
    }

    @GetMapping(value = "/translations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTextTranslation(@NotBlank @Size(max = 50) @RequestParam("text") String text,
                                                     @NotNull @RequestParam("lang") LanguageCode translateTo) {
        String translatedText = translator.translate(text, translateTo);
        return ResponseEntity.ok(translatedText);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponseBody handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(OpenAiTranslationErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponseBody handleOpenAiTranslationErrorException(IllegalArgumentException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, req.getRequestURI(), e);
    }
}
































