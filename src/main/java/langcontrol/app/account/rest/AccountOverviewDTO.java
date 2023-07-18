package langcontrol.app.account.rest;

import langcontrol.app.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountOverviewDTO {

    private long id;
    private String username;
    private boolean enabled;

    public static AccountOverviewDTO fromEntity(Account entity) {
        return new AccountOverviewDTO(entity.getId(), entity.getUsername(), entity.isEnabled());
    }
}
