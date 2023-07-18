package langcontrol.app.generator;

import langcontrol.app.generator.lexicala.LexicalaDictionary;
import langcontrol.app.generator.lexicala.pojo.LexicalaDictionaryApiSearchEntriesResponse;
import langcontrol.app.flashcard.PartOfSpeech;
import langcontrol.app.deck.LanguageCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class LexicalaDictionaryTest {

    @Autowired
    private LexicalaDictionary dictionary;

    @Mock
    private WebClient mockedWebClient;

    @Test
    void fetchTranslations_ShouldReturnListOfAvailableTranslations() {
        // given
        String sourceWord = "morgen";
        PartOfSpeech pos = PartOfSpeech.NOUN;
        String posString = pos.toString();
        LanguageCode sourceLang = LanguageCode.GERMAN;
        LanguageCode targetLang = LanguageCode.ENGLISH;
        String sourceLangCode = sourceLang.getCode();
        LexicalaDictionaryApiSearchEntriesResponse response = new LexicalaDictionaryApiSearchEntriesResponse();
//        given(mockedWebClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("https://lexicala1.p.rapidapi.com/search-entries")
//                        .queryParam("text", sourceWord)
//                        .queryParam("language", sourceLangCode)
//                        .queryParam("analyzed", "true")
//                        .queryParam("pos", posString)
//                        .build())
//                .header("X-RapidAPI-Key", "5be6450627msh84046dd86b3bc70p11fe4bjsn75205d365cd5")
//                .retrieve()
//                .bodyToMono(LexicalaDictionaryApiSearchEntriesResponse.class))
//            .willReturn();

        // when
//        Flux<String> result =  dictionary.fetchTranslations(sourceWord, sourceLang, targetLang, pos);

        // then

    }
}
