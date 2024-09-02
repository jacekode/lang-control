package langcontrol.app.account.rest;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUsernameDTO {

    private static final String USERNAME_INVALID_MSG = "Username is invalid.";
    private static final String USERNAME_TOO_SHORT_MSG = "Username should be at least 2 characters long.";
    private static final String USERNAME_TOO_LONG_MSG = "Username is too long.";
    private static final String USERNAME_PATTERN_MSG = "Username may only consist of letters, digits and underscores " +
            "but not two underscores next to each other.";

    @NotNull(message = USERNAME_INVALID_MSG)
    @NotBlank(message = USERNAME_INVALID_MSG)
    @Size(max = 30, message = USERNAME_TOO_LONG_MSG)
    @Size(min = 2, message = USERNAME_TOO_SHORT_MSG)
    @Pattern(regexp = "^(?!\\w*_{2,})\\w*$", message = USERNAME_PATTERN_MSG)
    private String username;
}
