package langcontrol.app.admin;

import jakarta.validation.Valid;
import langcontrol.app.account.Account;
import langcontrol.app.account.AccountRepository;
import langcontrol.app.exception.GeneralNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/admintools")
@Controller
public class AdminController {

    private final AdminService adminService;
    private final AccountRepository accountRepository;

    @Autowired
    public AdminController(AdminService adminService, AccountRepository accountRepository) {
        this.adminService = adminService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/users")
    public String showAllUsersPage(Model model) {
        List<UserOverviewDTO> allUsers = adminService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "all-users";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserPage(@PathVariable("id") long accountId,
                                   Model model) {
        Account foundAccount = accountRepository.findById(accountId)
                .orElseThrow(GeneralNotFoundException::new);
        EditUserDTO editUserDTO = new EditUserDTO(foundAccount, foundAccount.getUserProfile());
        model.addAttribute("userToEdit", editUserDTO);
        return "edit-user";
    }

    @PostMapping("/users/{id}/edit")
    public String handleEditUserRequest(@PathVariable("id") long accountId,
                                        @Valid @ModelAttribute("userToEdit") EditUserDTO editUserDTO) {
        adminService.editUser(accountId, editUserDTO);
        return "redirect:/admintools/users";
    }

    @PostMapping("/users/{id}/delete")
    public String handleDeleteUserRequest(@PathVariable("id") long accountId) {
        adminService.deleteUser(accountId);
        return "redirect:/admintools/users";
    }
}
