package langcontrol.app.account;

import langcontrol.app.account.rest.AccountOverviewDTO;
import langcontrol.app.account.rest.DeleteAccountDTO;
import langcontrol.app.account.rest.UpdatePasswordDTO;
import langcontrol.app.account.rest.UpdateUsernameDTO;

public interface AccountService {

    void registerNewUserAccount(AccountRegistrationDTO registrationDTO);

    AccountOverviewDTO updateUsername(UpdateUsernameDTO updateUsernameDTO);

    AccountOverviewDTO updatePassword(UpdatePasswordDTO updatePasswordDTO);

    void deleteAccount(DeleteAccountDTO deleteAccountDTO);

    Account retrieveCurrentAccount();

}
