package dev.jlynx.langcontrol.usersettings;

import dev.jlynx.langcontrol.usersettings.dto.UpdateUserSettingsRequest;
import dev.jlynx.langcontrol.usersettings.dto.UserSettingsOverview;

public interface UserSettingsService {

    UserSettingsOverview retrieveCurrentUserSettings();

    UserSettingsOverview updateCurrentUserSettings(UpdateUserSettingsRequest body);
}
