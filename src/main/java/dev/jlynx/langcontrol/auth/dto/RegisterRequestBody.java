package dev.jlynx.langcontrol.auth.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A data transfer object which represents the body of an account registration request. It contains detailed
 * account creation validation constraints.
 *
 * @param username the desired username of the account being registered
 * @param password the account's password set by user
 * @param firstName the user's first name
 */
public record RegisterRequestBody(
        @NotNull(message = ErrorMsg.USERNAME_NOT_PROVIDED)
        @NotBlank(message = ErrorMsg.USERNAME_NOT_PROVIDED)
        @Size(min = Constraints.USERNAME_MIN, message = ErrorMsg.USERNAME_TOO_SHORT)
        @Size(max = Constraints.USERNAME_MAX, message = ErrorMsg.USERNAME_TOO_LONG)
        @Pattern(regexp = Constraints.USERNAME_REGEX, message = ErrorMsg.USERNAME_PATTERN)
        String username,

        @NotNull(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(min = Constraints.PWD_MIN, message = ErrorMsg.PASSWORD_TOO_SHORT)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        @Pattern(regexp = Constraints.PWD_REGEX, message = ErrorMsg.PASSWORD_PATTERN)
        String password,

        @NotNull(message = ErrorMsg.NAME_NOT_PROVIDED)
        @NotBlank(message = ErrorMsg.NAME_NOT_PROVIDED)
        @Size(max = Constraints.FIRSTNAME_MAX, message = ErrorMsg.NAME_TOO_LONG)
        @Pattern(regexp = Constraints.FIRSTNAME_REGEX, message = ErrorMsg.NAME_PATTERN)
        String firstName
) {}
