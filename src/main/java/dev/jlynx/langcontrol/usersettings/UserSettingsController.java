package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.usersettings.dto.UpdateUserSettingsRequest;
import dev.jlynx.langcontrol.usersettings.dto.UserSettingsOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${apiPref}/settings")
@RestController
public class UserSettingsController {

    public static final Logger LOG = LoggerFactory.getLogger(UserSettingsController.class);

    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public ResponseEntity<UserSettingsOverview> getSettings() {
        LOG.debug("getSettings() method invoked.");
        UserSettingsOverview userSettingsOverview = userSettingsService.retrieveCurrentUserSettings();
        return ResponseEntity.ok(userSettingsOverview);
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody UpdateUserSettingsRequest body) {
        UserSettingsOverview overview = userSettingsService.updateCurrentUserSettings(body);
        return ResponseEntity.ok(overview);
    }
}
