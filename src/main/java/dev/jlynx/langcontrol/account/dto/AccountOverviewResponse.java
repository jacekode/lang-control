package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.role.Role;

import java.util.List;

public record AccountOverviewResponse(
        long id,
        String username,
        boolean accountNonLocked,
        boolean enabled,
        List<String> roles
) {

    public static AccountOverviewResponse fromEntity(Account entity) {
        return new AccountOverviewResponse(
                entity.getId(),
                entity.getUsername(),
                entity.isAccountNonLocked(),
                entity.isEnabled(),
                entity.getRoles().stream().map(Role::getValue).toList()
        );
    }
}
