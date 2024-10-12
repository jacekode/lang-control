package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.userprofile.UserProfile;
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
        this.name = userProfile.getFirstName();
        this.enabled = account.isEnabled();
    }
}
