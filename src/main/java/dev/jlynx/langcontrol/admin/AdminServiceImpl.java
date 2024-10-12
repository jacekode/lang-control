package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;

    @Autowired
    public AdminServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // todo: remove the stream filtering and add findByRole method to AccountRepository
    @Transactional
    @Override
    public List<UserOverviewDTO> getAllUsers() {
        List<Account> allAccounts = accountRepository.findAll();

        List<UserOverviewDTO> dtoList = allAccounts.stream()
                .filter(account -> account.getRoles().stream()
                        .noneMatch(r -> Objects.equals(r.getValue(), DefinedRoleValue.ADMIN.getValue())))
                .map(account -> {
                    UserProfile profile = account.getUserProfile();
                    return new UserOverviewDTO(account, profile);
                })
                .toList();
        return dtoList;
    }

    @Transactional
    @Override
    public void editUser(long accountId, EditUserDTO dto) {
        Account accountToEdit = accountRepository.findById(accountId)
                .orElseThrow(AssetNotFoundException::new);
        UserProfile profileToEdit = accountToEdit.getUserProfile();
        accountToEdit.setUsername(dto.getUsername());
        accountToEdit.setEnabled(dto.isEnabled());
        profileToEdit.setFirstName(dto.getName());
    }

    @Transactional
    @Override
    public void deleteUser(long accountId) {
        Account accountToDelete = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssetNotFoundException(
                        "Account with id=" + accountId + "couldn't be found."));
        accountRepository.delete(accountToDelete);
    }
}
