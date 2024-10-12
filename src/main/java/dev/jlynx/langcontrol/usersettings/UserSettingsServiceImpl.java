package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import dev.jlynx.langcontrol.usersettings.dto.UpdateUserSettingsRequest;
import dev.jlynx.langcontrol.usersettings.dto.UserSettingsOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserProfileService userProfileService;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsServiceImpl(UserProfileService userProfileService,
                                   UserSettingsRepository userSettingsRepository) {
        this.userProfileService = userProfileService;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Transactional
    @Override
    public UserSettingsOverview retrieveCurrentUserSettings() {
        UserProfile curProfile = userProfileService.retrieveCurrentProfileEntity();
        UserSettings curSettings = curProfile.getUserSettings();
        return UserSettingsOverview.fromEntity(curSettings);
    }

    @Transactional
    @Override
    public UserSettingsOverview updateCurrentUserSettings(UpdateUserSettingsRequest body) {
        UserProfile curProfile = userProfileService.retrieveCurrentProfileEntity();
        UserSettings curSettings = curProfile.getUserSettings();
        curSettings.setDynamicSentencesOnByDefault(body.dynamicSentencesOnByDefault());
        curSettings.setZenModeEnabled(body.zenModeEnabled());
        userSettingsRepository.save(curSettings);
        return UserSettingsOverview.fromEntity(curSettings);
    }
}
