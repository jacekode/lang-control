package langcontrol.app.userprofile;

import langcontrol.app.usersettings.UserSettings;

public interface UserProfileService {

    void updateUserSettings(UserSettings newUserSettings);

    UserProfile retrieveCurrentUserProfile();
}
