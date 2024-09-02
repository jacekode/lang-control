package langcontrol.app.account;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegistrationDTO {

    private static final String USERNAME_INVALID_MSG = "Username is invalid.";
    private static final String USERNAME_TOO_SHORT_MSG = "Username should be at least 2 characters long.";
    private static final String USERNAME_TOO_LONG_MSG = "Username is too long.";
    private static final String USERNAME_PATTERN_MSG = "Username may only consist of letters, digits and underscores " +
            "but not two underscores next to each other.";

    private static final String PASSWORD_INVALID_MSG = "Password is invalid.";
    private static final String PASSWORD_TOO_SHORT_MSG = "Password is too short.";
    private static final String PASSWORD_TOO_LONG_MSG = "Password is too long.";
    private static final String PASSWORD_PATTERN_MSG = "The password should be at least 8 characters long " +
            "and contain at least one lowercase letter, uppercase letter, digit and special symbol.";

    private static final String NAME_INVALID_MSG = "Name is invalid.";
    private static final String NAME_TOO_LONG_MSG = "Name is too long.";
    private static final String NAME_PATTERN_MSG = "Name may only contain letters.";

    @NotNull(message = USERNAME_INVALID_MSG)
    @NotBlank(message = USERNAME_INVALID_MSG)
    @Size(max = 30, message = USERNAME_TOO_LONG_MSG)
    @Size(min = 2, message = USERNAME_TOO_SHORT_MSG)
    @Pattern(regexp = "^(?!\\w*_{2,})\\w*$", message = USERNAME_PATTERN_MSG)
    private String username;

    @NotNull(message = PASSWORD_INVALID_MSG)
    @NotBlank(message = PASSWORD_INVALID_MSG)
    @Size(min = 8, message = PASSWORD_TOO_SHORT_MSG)
    @Size(max = 50, message = PASSWORD_TOO_LONG_MSG)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$",
            message = PASSWORD_PATTERN_MSG)
    private String password;

    @NotNull(message = NAME_INVALID_MSG)
    @NotBlank(message = NAME_INVALID_MSG)
    @Size(max = 50, message = NAME_TOO_LONG_MSG)
    @Pattern(regexp = "^[a-zA-Z]*$", message = NAME_PATTERN_MSG)
    private String name;
}
