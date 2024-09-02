package langcontrol.app.usersettings;

import langcontrol.app.account.Account;
import langcontrol.app.account.AccountService;
import langcontrol.app.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SettingsController {

    private final UserProfileService userProfileService;
    private final UserSettingsService userSettingsService;
    private final AccountService accountService;

    @Autowired
    public SettingsController(UserProfileService userProfileService, UserSettingsService userSettingsService,
                              AccountService accountService) {
        this.userProfileService = userProfileService;
        this.userSettingsService = userSettingsService;
        this.accountService = accountService;
    }

    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        Account currentAccount = accountService.retrieveCurrentAccount();
        model.addAttribute("currentAccount", currentAccount);
        model.addAttribute("userSettings", userSettingsService.retrieveCurrentUserSettings());
        return "settings";
    }

    @PostMapping("/settings/general")
    public String saveGeneralSettings(@ModelAttribute("userSettings") UserSettings userSettings) {
        userProfileService.updateUserSettings(userSettings);
        return "redirect:/settings";
    }
}
