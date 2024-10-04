package dev.jlynx.langcontrol.account.dto;

import dev.jlynx.langcontrol.util.Constraints;
import dev.jlynx.langcontrol.util.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        String currentPassword,

        @NotBlank(message = ErrorMsg.PASSWORD_NOT_PROVIDED)
        @Size(min = Constraints.PWD_MIN, message = ErrorMsg.PASSWORD_TOO_SHORT)
        @Size(max = Constraints.PWD_MAX, message = ErrorMsg.PASSWORD_TOO_LONG)
        @Pattern(regexp = Constraints.PWD_REGEX,
                message = ErrorMsg.PASSWORD_PATTERN)
        String newPassword
) {}
