package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.account.Account;

public record AccountOverviewResponse(
        long id,
        String username,
        boolean accountNonLocked,
        boolean enabled
) {

    public static AccountOverviewResponse fromEntity(Account entity) {
        return new AccountOverviewResponse(
                entity.getId(),
                entity.getUsername(),
                entity.isAccountNonLocked(),
                entity.isEnabled()
        );
    }
}
