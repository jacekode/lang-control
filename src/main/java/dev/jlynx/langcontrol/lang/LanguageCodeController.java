package dev.jlynx.langcontrol.lang;

import dev.jlynx.langcontrol.lang.dto.LanguageCodeOverview;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${apiPref}/lang")
public class LanguageCodeController {

    @GetMapping
    public ResponseEntity<List<LanguageCodeOverview>> getAllAvailableLanguageCodes() {
        List<LanguageCodeOverview> langCodeOverviewList = Arrays.stream(LanguageCode.values())
                .map(LanguageCodeOverview::fromLanguageCode)
                .toList();
        return ResponseEntity.ok(langCodeOverviewList);
    }
}
