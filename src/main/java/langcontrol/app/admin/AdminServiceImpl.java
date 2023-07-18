package langcontrol.app.admin;

import langcontrol.app.account.Account;
import langcontrol.app.account.AccountRepository;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.security.DefinedRoleValue;
import langcontrol.app.user_profile.UserProfile;
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
    public void editUser(long accountId, EditUserDTO editUserDTO) {
        Account accountToEdit = accountRepository.findById(accountId)
                .orElseThrow(GeneralNotFoundException::new);
        UserProfile profileToEdit = accountToEdit.getUserProfile();

        accountToEdit.setUsername(editUserDTO.getUsername());
        accountToEdit.setEnabled(editUserDTO.isEnabled());
        profileToEdit.setName(editUserDTO.getName());
    }

    @Transactional
    @Override
    public void deleteUser(long accountId) {
        Account accountToDelete = accountRepository.findById(accountId).orElseThrow(() -> new GeneralNotFoundException(
                "Account with id=" + accountId + "couldn't be found."));

        accountRepository.delete(accountToDelete);
    }
}
