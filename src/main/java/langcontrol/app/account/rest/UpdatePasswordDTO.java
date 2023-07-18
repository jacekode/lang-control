package langcontrol.app.account.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePasswordDTO {

    private static final String PASSWORD_INVALID_MSG = "Password is invalid.";
    private static final String PASSWORD_TOO_SHORT_MSG = "Password is too short.";
    private static final String PASSWORD_TOO_LONG_MSG = "Password is too long.";
    private static final String PASSWORD_PATTERN_MSG = "The password should be at least 8 characters long " +
            "and contain at least one lowercase letter, uppercase letter, digit and special symbol.";


    private String currentPassword;

    @NotNull(message = PASSWORD_INVALID_MSG)
    @NotBlank(message = PASSWORD_INVALID_MSG)
    @Size(min = 8, message = PASSWORD_TOO_SHORT_MSG)
    @Size(max = 50, message = PASSWORD_TOO_LONG_MSG)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$",
            message = PASSWORD_PATTERN_MSG)
    private String newPassword;
}
