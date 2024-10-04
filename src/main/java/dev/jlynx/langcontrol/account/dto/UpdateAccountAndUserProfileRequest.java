package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents the body of a request to update {@code Account} and {@code UserProfile} fields.
 *
 * @param username the new value for the {@link Account}'s username field
 * @param firstName the new value for the {@link  UserProfile}'s firstName field
 */
public record UpdateAccountAndUserProfileRequest(
        @NotBlank(message = ErrorMsg.USERNAME_NOT_PROVIDED)
        @Size(min = Constraints.USERNAME_MIN, message = ErrorMsg.USERNAME_TOO_SHORT)
        @Size(max = Constraints.USERNAME_MAX, message = ErrorMsg.USERNAME_TOO_LONG)
        @Pattern(regexp = Constraints.USERNAME_REGEX, message = ErrorMsg.USERNAME_PATTERN)
        String username,

        @NotBlank(message = ErrorMsg.NAME_NOT_PROVIDED)
        @Size(max = Constraints.FIRSTNAME_MAX, message = ErrorMsg.NAME_TOO_LONG)
        @Pattern(regexp = Constraints.FIRSTNAME_REGEX, message = ErrorMsg.NAME_PATTERN)
        String firstName
) {}
