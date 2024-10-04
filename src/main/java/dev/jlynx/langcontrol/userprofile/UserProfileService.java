package dev.jlynx.langcontrol.userprofile;

import dev.jlynx.langcontrol.account.dto.UpdateAccountAndUserProfileRequest;
import dev.jlynx.langcontrol.exception.ValuesTheSameException;
import dev.jlynx.langcontrol.userprofile.dto.UserProfileOverview;

public interface UserProfileService {

    UserProfile retrieveCurrentProfileEntity();

    UserProfileOverview getCurrentUserProfile();

    /**
     * Updates the {@code firstName} field of the currently authenticated account's {@code UserProfile}.
     *
     * @param body the request body object; usually received from a controller
     * @throws ValuesTheSameException if the new {@code firstName} value doesn't differ from the current one
     */
    void updateCurrentProfile(UpdateAccountAndUserProfileRequest body);
}
