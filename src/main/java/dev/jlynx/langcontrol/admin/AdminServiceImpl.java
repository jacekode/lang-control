package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;

    @Autowired
    public AdminServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
    public void deleteAccount(long accountId) {
        Account accountToDelete = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssetNotFoundException(
                        "Account with id=" + accountId + " couldn't be found."));
        accountRepository.deleteById(accountId);
    }
}
