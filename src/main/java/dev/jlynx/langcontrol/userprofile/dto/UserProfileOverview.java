package dev.jlynx.langcontrol.userprofile.dto;

import dev.jlynx.langcontrol.userprofile.UserProfile;

public record UserProfileOverview(long id, String firstName) {

    public static UserProfileOverview fromEntity(UserProfile entity) {
        return new UserProfileOverview(entity.getId(), entity.getFirstName());
    }
}
