package dev.jlynx.langcontrol.account;

import dev.jlynx.langcontrol.account.dto.*;
import dev.jlynx.langcontrol.auth.dto.RegisterRequestBody;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.exception.UsernameAlreadyExistsException;
import dev.jlynx.langcontrol.exception.ValuesTheSameException;
import dev.jlynx.langcontrol.exception.WrongPasswordException;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.role.RoleRepository;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.usersettings.UserSettings;
import dev.jlynx.langcontrol.usersettings.UserSettingsRepository;
import dev.jlynx.langcontrol.util.AuthRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    public static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserSettingsRepository userSettingsRepository;
    private final AuthRetriever authRetriever;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder encoder,
                              UserSettingsRepository userSettingsRepository,
                              AuthRetriever authRetriever) {
        this.accountRepository = accountRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.authRetriever = authRetriever;
    }

    @Transactional
    @Override
    public AccountOverviewResponse registerAccount(RegisterRequestBody body) {
        Optional<Account> accountOptional = accountRepository.findByUsername(body.username());
        if (accountOptional.isPresent()) {
            LOG.debug("Failed to register a new account because of an already existing username: '{}'", body.username());
            throw new UsernameAlreadyExistsException("Another account with username '%s' already exists.".formatted(body.username()));
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
                body.username(),
                encoder.encode(body.password()),
                roles,
                true,
                true,
                true,
                true);

        UserProfile userProfileToCreate = new UserProfile(
                null,
                body.firstName());

        UserSettings userSettings = UserSettings.withDefaults();
        userSettingsRepository.save(userSettings);

        userProfileToCreate.setUserSettings(userSettings);
        userProfileToCreate.setDecks(new ArrayList<>());
        userProfileToCreate.setAccount(accountToCreate);
        accountToCreate.setUserProfile(userProfileToCreate);

        Account savedAccount = accountRepository.save(accountToCreate);
        LOG.debug("Account with id={} persisted to database.", savedAccount.getId());
        return AccountOverviewResponse.fromEntity(savedAccount);
    }

    @Transactional
    @Override
    public AccountOverviewResponse updateAccount(UpdateAccountAndUserProfileRequest body) {
        Account currentAccount = this.retrieveCurrentAccountEntity();
        Optional<Account> accountOptional = accountRepository.findByUsername(body.username());
        if (accountOptional.isPresent() && !Objects.equals(body.username(), currentAccount.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already taken.");
        }
        currentAccount.setUsername(body.username());
        accountRepository.save(currentAccount);

        Authentication curAuthentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = curAuthentication.getAuthorities();
        Object credentials = curAuthentication.getCredentials();
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(body.username(), credentials, authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return AccountOverviewResponse.fromEntity(currentAccount);
    }

    @Transactional
    @Override
    public AccountOverviewResponse changePassword(UpdatePasswordRequest body) {
        Account currentAccount = this.retrieveCurrentAccountEntity();
        if (!encoder.matches(body.currentPassword(), currentAccount.getPassword())) {
            throw new WrongPasswordException("The value for the current password is incorrect.");
        }
        currentAccount.setPassword(encoder.encode(body.newPassword()));
        accountRepository.save(currentAccount);
        return AccountOverviewResponse.fromEntity(currentAccount);
    }

    @Transactional
    @Override
    public void deleteAccount(DeleteAccountRequest body) {
        Account currentAccount = this.retrieveCurrentAccountEntity();
        if (!encoder.matches(body.password(), currentAccount.getPassword())) {
            throw new WrongPasswordException("Password is incorrect.");
        }
        accountRepository.deleteById(currentAccount.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    // todo: perhaps, in the future, move the implementation of the principalRetriever.retrieveAccount() to this method directly?
    /**
     * {@inheritDoc}
     * This particular implementation makes use of the {@link AuthRetriever}.
     *
     * @see AuthRetriever
     */
    @Transactional
    @Override
    public Account retrieveCurrentAccountEntity() {
        Account principalAccount = authRetriever.retrieveCurrentAccount();
        Account currentAccount = accountRepository.findById(principalAccount.getId())
                .orElseThrow(() -> new AssetNotFoundException(
                        String.format("Account with id=%d couldn't be found.", principalAccount.getId())
                ));
        return currentAccount;
    }

    @Override
    public AccountOverviewResponse getCurrentAccount() {
        Account account = authRetriever.retrieveCurrentAccount();
        return AccountOverviewResponse.fromEntity(account);
    }
}
