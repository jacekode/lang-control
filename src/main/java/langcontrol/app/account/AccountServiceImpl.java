package langcontrol.app.account;

import langcontrol.app.account.rest.AccountOverviewDTO;
import langcontrol.app.account.rest.DeleteAccountDTO;
import langcontrol.app.account.rest.UpdatePasswordDTO;
import langcontrol.app.account.rest.UpdateUsernameDTO;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.exception.UsernameAlreadyExistsException;
import langcontrol.app.exception.UsernamesTheSameException;
import langcontrol.app.exception.PasswordMismatchException;
import langcontrol.app.security.DefinedRoleValue;
import langcontrol.app.security.Role;
import langcontrol.app.security.RoleRepository;
import langcontrol.app.userprofile.UserProfile;
import langcontrol.app.usersettings.UserSettings;
import langcontrol.app.usersettings.UserSettingsRepository;
import langcontrol.app.util.PrincipalRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder encoder,
                              UserSettingsRepository userSettingsRepository) {
        this.accountRepository = accountRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Transactional
    @Override
    public void registerNewUserAccount(AccountRegistrationDTO registrationDTO) {
        Optional<Account> accountOptional = accountRepository.findByUsername(registrationDTO.getUsername());
        if (accountOptional.isPresent()) {
            throw new UsernameAlreadyExistsException("The account with the given username already exists.");
        }
        Optional<Role> roleOptional = roleRepository.findByValue(DefinedRoleValue.USER.getValue());
        Role userRole;
        if (roleOptional.isEmpty()) {
            Role newUserRole = new Role(null, DefinedRoleValue.USER);
            userRole = roleRepository.save(newUserRole);
        } else {
            userRole = roleOptional.get();
        }
        List<Role> roles = List.of(userRole);

        Account accountToCreate = new Account(
                null,
                registrationDTO.getUsername(),
                encoder.encode(registrationDTO.getPassword()),
                roles,
                true,
                true,
                true,
                true);

        UserProfile userProfileToCreate = new UserProfile(
                null,
                registrationDTO.getName());

        UserSettings userSettings = UserSettings.withDefaults();
        userSettingsRepository.save(userSettings);

        userProfileToCreate.setUserSettings(userSettings);
        userProfileToCreate.setDecks(new ArrayList<>());
        userProfileToCreate.setAccount(accountToCreate);
        accountToCreate.setUserProfile(userProfileToCreate);

        accountRepository.save(accountToCreate);
    }

    @Transactional
    @Override
    public AccountOverviewDTO updateUsername(UpdateUsernameDTO dto) {
        Account currentAccount = this.retrieveCurrentAccount();
        String newUsername = dto.getUsername();
        if (Objects.equals(newUsername, currentAccount.getUsername())) {
            throw new UsernamesTheSameException("The username should be different.");
        }
        Optional<Account> accountOptional = accountRepository.findByUsername(newUsername);
        if (accountOptional.isPresent()) {
            throw new UsernameAlreadyExistsException("The username is already taken.");
        }
        currentAccount.setUsername(newUsername);
        accountRepository.save(currentAccount);
        return AccountOverviewDTO.fromEntity(currentAccount);
    }

    @Transactional
    @Override
    public AccountOverviewDTO updatePassword(UpdatePasswordDTO dto) {
        Account currentAccount = this.retrieveCurrentAccount();
        if (!encoder.matches(dto.getCurrentPassword(), currentAccount.getPassword())) {
            throw new PasswordMismatchException("Password is incorrect.");
        }
        currentAccount.setPassword(encoder.encode(dto.getNewPassword()));
        accountRepository.save(currentAccount);
        return AccountOverviewDTO.fromEntity(currentAccount);
    }

    @Transactional
    @Override
    public void deleteAccount(DeleteAccountDTO dto) {
        Account currentAccount = this.retrieveCurrentAccount();
        if (!encoder.matches(dto.getPassword(), currentAccount.getPassword())) {
            throw new PasswordMismatchException("Password is incorrect.");
        }
        accountRepository.deleteById(currentAccount.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Transactional
    @Override
    public Account retrieveCurrentAccount() {
        Account principalAccount = PrincipalRetriever.retrieveAccount();
        Account currentAccount = accountRepository.findById(principalAccount.getId())
                .orElseThrow(() -> {
                    return new GeneralNotFoundException(String.format("Account with id=%d couldn't be found.",
                            principalAccount.getId()));
                });
        return currentAccount;
    }
}
