package dev.jlynx.langcontrol.generator;

import dev.jlynx.langcontrol.generator.dto.TranslationRequest;
import dev.jlynx.langcontrol.generator.dto.TranslationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.lang.LanguageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("${apiPref}/generate")
public class GeneratorController {

    private final Logger LOG = LoggerFactory.getLogger(GeneratorController.class);

    private final Dictionary dictionary;
    private final SentenceGenerator sentenceGenerator;
    private final Translator translator;

    @Autowired
    public GeneratorController(
            Dictionary dictionary,
            SentenceGenerator sentenceGenerator,
            @Qualifier("deeplTranslator") Translator translator
    ) {
        this.dictionary = dictionary;
        this.sentenceGenerator = sentenceGenerator;
        this.translator = translator;
    }

    // todo: change GET requests to POST requests
    @GetMapping(value = "/dictionary")
    public ResponseEntity<List<String>> getWordTranslations(
            @NotBlank @Size(max = 80) @RequestParam("word") String wordToTranslate,
            @RequestParam("from") LanguageCode translateFrom,
            @RequestParam("to") LanguageCode translateTo,
            @RequestParam("pos") PartOfSpeech partOfSpeech
    ) {
        LOG.debug("getWordTranslations() method was called with parameters: " +
                        "wordToTranslate={} translateFrom={} translateTo={} partOfSpeech={}",
                wordToTranslate, translateFrom, translateTo, partOfSpeech);
        List<String> translations = dictionary.getTranslationsList(
                wordToTranslate, translateFrom, translateTo, partOfSpeech
        );
        return ResponseEntity.ok(translations);
    }

    @GetMapping(value = "/sentences")
    public ResponseEntity<List<String>> generateSentences(
            @NotBlank @Size(max = 80) @RequestParam("keyword") String keyword,
            @RequestParam("lang") LanguageCode keywordLang,
            @RequestParam("pos") PartOfSpeech keywordPos,
            @Max(3) @RequestParam(value = "num", required = false, defaultValue = "1") int numOfSentences
    ) {
        LOG.debug("generateSentences() method was called with parameters: " +
                        "keyword={} keywordLang={} keywordPos={} numOfSentences={}",
                keyword, keywordLang, keywordPos, numOfSentences);
        List<String> sentences = sentenceGenerator.generate(keyword, keywordLang, keywordPos, numOfSentences);
        return ResponseEntity.ok(sentences);
    }

    @PostMapping(value = "/translate")
    public ResponseEntity<TranslationResponse> translateText(@Valid @RequestBody TranslationRequest body) {
        LOG.debug("translateText() request body={}", body);
        String translation = translator.translate(body.text(), body.translateTo(), body.translateFrom());
        return ResponseEntity.ok(new TranslationResponse(translation));
    }
}
