package dev.jlynx.langcontrol.admin.dto;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.userprofile.UserProfile;

import java.util.List;

public record UserOverview(
        long accountId,
        String username,
        String firstName,
        boolean enabled,
        boolean nonLocked,
        List<String> roles
) {

    public UserOverview(Account account, UserProfile profile) {
        this(
                account.getId(),
                account.getUsername(),
                profile.getFirstName(),
                account.isEnabled(),
                account.isAccountNonLocked(),
                account.getRoles().stream().map(Role::getValue).toList()
        );
    }
}
