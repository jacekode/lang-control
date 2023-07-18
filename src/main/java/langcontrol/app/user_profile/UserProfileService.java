package langcontrol.app.user_profile;

import langcontrol.app.user_settings.UserSettings;

public interface UserProfileService {

    void updateUserSettings(UserSettings newUserSettings);

    UserProfile retrieveCurrentUserProfile();
}
