package dev.jlynx.langcontrol.admin.dto;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.userprofile.UserProfile;

public record UserOverview(
        long accountId,
        String username,
        String firstName,
        boolean enabled,
        boolean nonLocked
) {

    public UserOverview(Account account, UserProfile profile) {
        this(
                account.getId(),
                account.getUsername(),
                profile.getFirstName(),
                account.isEnabled(),
                account.isAccountNonLocked()
        );
    }
}
