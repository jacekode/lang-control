package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.admin.dto.DeleteAccountAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdatePasswordAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RequestMapping("${apiPref}/admin")
@RestController
public class AdminController {

    private static Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserOverview>> getAllUsers() {
        List<UserOverview> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserOverview> getUserById(@Min(1) @PathVariable("id") long accountId) {
        UserOverview userOverview = adminService.getUserById(accountId);
        return ResponseEntity.ok(userOverview);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(
            @Min(1) @PathVariable("id") long accountId,
            @RequestBody @Valid UpdateUserRequest body
    ) {
        adminService.updateUser(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/users/{id}/password")
    public ResponseEntity<Void> updateUserPassword(@Min(1) @PathVariable("id") long accountId,
                                                   @Valid @RequestBody UpdatePasswordAdminRequest body) {
        LOG.debug("updateUserPassword() method invoked");
        adminService.overwriteUserPassword(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserAccount(@Min(1) @PathVariable("id") long accountId,
                                                  @Valid @RequestBody DeleteAccountAdminRequest body) {
        adminService.deleteAccount(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
