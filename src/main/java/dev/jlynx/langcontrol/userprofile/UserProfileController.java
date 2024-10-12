package dev.jlynx.langcontrol.userprofile;

import dev.jlynx.langcontrol.userprofile.dto.UserProfileOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("${apiPref}/profile")
@RestController
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileOverview> getCurrentUserProfile() {
        UserProfileOverview overview = userProfileService.getCurrentUserProfile();
        return ResponseEntity.ok(overview);
    }
}
