package langcontrol.app.user_profile;

import langcontrol.app.account.Account;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.user_settings.UserSettings;
import langcontrol.app.util.PrincipalRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    @Override
    public void updateUserSettings(UserSettings newUserSettings) {
        UserProfile currentProfile = this.retrieveCurrentUserProfile();
        currentProfile.setUserSettings(newUserSettings);
    }

    @Transactional
    @Override
    public UserProfile retrieveCurrentUserProfile() {
        Account currentAccount = PrincipalRetriever.retrieveAccount();
        UserProfile currentUserProfile = userProfileRepository.findByAccount_Id(currentAccount.getId())
                .orElseThrow(() -> new GeneralNotFoundException("The user profile for current account couldn't be found."));
        return currentUserProfile;
    }
}
