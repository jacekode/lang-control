package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.account.AccountService;
import dev.jlynx.langcontrol.admin.dto.DeleteAccountAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdatePasswordAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.exception.WrongPasswordException;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final PasswordEncoder encoder;

    @Autowired
    public AdminServiceImpl(AccountRepository accountRepository, AccountService accountService, PasswordEncoder encoder) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.encoder = encoder;
    }

    @Override
    public List<UserOverview> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> {
                    UserProfile profile = account.getUserProfile();
                    return new UserOverview(account, profile);
                })
                .toList();
    }

    @Override
    public UserOverview getUserById(long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = account.getUserProfile();
        return new UserOverview(account, profile);
    }

    @Transactional
    @Override
    public void updateUser(long accountId, UpdateUserRequest body) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AssetNotFoundException::new);
        UserProfile profile = account.getUserProfile();
        account.setUsername(body.username());
        profile.setFirstName(body.firstName());
        account.setEnabled(body.enabled());
        account.setAccountNonLocked(body.nonLocked());
    }

    @Transactional
    @Override
    public void overwriteUserPassword(long accountId, UpdatePasswordAdminRequest body) {
        LOG.debug("overwriteUserPassword() method invoked.");
        Account currentAccount = accountService.retrieveCurrentAccountEntity();
        if (!encoder.matches(body.adminPassword(), currentAccount.getPassword())) {
            throw new WrongPasswordException("The value for the current password is incorrect.");
        }
        Account accountToUpdate = accountRepository.findById(accountId).orElseThrow(AssetNotFoundException::new);
        accountToUpdate.setPassword(encoder.encode(body.newPassword()));
        accountRepository.save(accountToUpdate);
    }

    @Transactional
    @Override
    public void deleteAccount(long accountId, DeleteAccountAdminRequest body) {
        Account currentAccount = accountService.retrieveCurrentAccountEntity();
        if (!encoder.matches(body.adminPassword(), currentAccount.getPassword())) {
            throw new WrongPasswordException("The value for the current password is incorrect.");
        }
        Account accountToDelete = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssetNotFoundException(
                        "Account with id=" + accountId + " couldn't be found."));
        accountRepository.deleteById(accountId);
    }
}
