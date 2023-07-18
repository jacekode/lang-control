package langcontrol.app.admin;

import langcontrol.app.account.Account;
import langcontrol.app.user_profile.UserProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserOverviewDTO {

    private long accountId;
    private String username;
    private String name;
    private boolean enabled;

    public UserOverviewDTO(Account account, UserProfile userProfile) {
        this.accountId = account.getId();
        this.username = account.getUsername();
        this.name = userProfile.getName();
        this.enabled = account.isEnabled();
    }
}
