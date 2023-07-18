package langcontrol.app.account.rest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class DeleteAccountDTO {

    @NotNull(message = "Password cannot be null")
    private String password;
}
