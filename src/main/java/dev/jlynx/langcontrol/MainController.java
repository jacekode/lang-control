package dev.jlynx.langcontrol;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("${apiPref}")
@RestController
public class MainController {

    /**
     * The sole purpose of this empty, bodiless, operation-less is to set
     * the csrf token inside a XSRF-TOKEN cookie.
     *
     * @return {@code ResponseEntity} with OK status code and without body
     */
    @PostMapping
    public ResponseEntity<Void> emptyRequest() {
        return ResponseEntity.ok().build();
    }
}
