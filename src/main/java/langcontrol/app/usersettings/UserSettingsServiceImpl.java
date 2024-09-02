package langcontrol.app.usersettings;

import langcontrol.app.account.Account;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.userprofile.UserProfile;
import langcontrol.app.userprofile.UserProfileRepository;
import langcontrol.app.util.PrincipalRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserSettingsServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    @Override
    public UserSettings retrieveCurrentUserSettings() {
        Account currentAccount = PrincipalRetriever.retrieveAccount();
        UserProfile currentProfile = userProfileRepository.findByAccount_Id(currentAccount.getId())
                .orElseThrow(GeneralNotFoundException::new);
        return currentProfile.getUserSettings();
    }
}
