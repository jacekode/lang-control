package dev.jlynx.langcontrol.usersettings.dto;

import dev.jlynx.langcontrol.usersettings.UserSettings;

public record UserSettingsOverview(long id, boolean dynamicSentencesOnByDefault, boolean zenModeEnabled) {

    public static UserSettingsOverview fromEntity(UserSettings entity) {
        return new UserSettingsOverview(
                entity.getId(),
                entity.isDynamicSentencesOnByDefault(),
                entity.isZenModeEnabled()
        );
    }
}
