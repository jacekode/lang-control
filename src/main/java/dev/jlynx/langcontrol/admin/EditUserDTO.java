package dev.jlynx.langcontrol.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class EditUserDTO {

    private static final String USERNAME_INVALID_MSG = "Username is invalid.";
    private static final String USERNAME_TOO_SHORT_MSG = "Username should be at least 2 characters long.";
    private static final String USERNAME_TOO_LONG_MSG = "Username is too long.";
    private static final String USERNAME_PATTERN_MSG = "Username may only consist of letters, digits and underscores " +
            "but not two underscores next to each other.";

    private static final String NAME_INVALID_MSG = "Name is invalid.";
    private static final String NAME_TOO_LONG_MSG = "Name is too long.";
    private static final String NAME_PATTERN_MSG = "Name may only contain letters.";

    private long accountId;

    @NotNull(message = USERNAME_INVALID_MSG)
    @NotBlank(message = USERNAME_INVALID_MSG)
    @Size(max = 30, message = USERNAME_TOO_LONG_MSG)
    @Size(min = 2, message = USERNAME_TOO_SHORT_MSG)
    @Pattern(regexp = "^(?!\\w*_{2,})\\w*$", message = USERNAME_PATTERN_MSG)
    private String username;

    @NotNull(message = NAME_INVALID_MSG)
    @NotBlank(message = NAME_INVALID_MSG)
    @Size(max = 50, message = NAME_TOO_LONG_MSG)
    @Pattern(regexp = "^[a-zA-Z]*$", message = NAME_PATTERN_MSG)
    private String name;

    private boolean enabled;

    public EditUserDTO(Account account, UserProfile userProfile) {
        this.accountId = account.getId();
        this.username = account.getUsername();
        this.name = userProfile.getFirstName();
        this.enabled = account.isEnabled();
    }
}
