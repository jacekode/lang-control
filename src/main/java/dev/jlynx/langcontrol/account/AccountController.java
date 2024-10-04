package dev.jlynx.langcontrol.account;

import dev.jlynx.langcontrol.account.dto.*;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import dev.jlynx.langcontrol.exception.ErrorResponseBody;
import dev.jlynx.langcontrol.exception.ValuesTheSameException;
import dev.jlynx.langcontrol.exception.WrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${apiPref}/account")
@RestController
public class AccountController {

    private final AccountService accountService;
    private final UserProfileService userProfileService;

    @Autowired
    public AccountController(AccountService accountService, UserProfileService userProfileService) {
        this.accountService = accountService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<AccountOverviewResponse> getCurrentAccount() {
        AccountOverviewResponse overview = accountService.getCurrentAccount();
        return ResponseEntity.ok(overview);
    }

    @PutMapping
    public ResponseEntity<AccountOverviewResponse> updateAccountAndUserProfile(@Valid @RequestBody UpdateAccountAndUserProfileRequest body) {
        var updatedAccountOverview = accountService.updateAccount(body);
        userProfileService.updateCurrentProfile(body);
        return ResponseEntity.ok(updatedAccountOverview);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest body) {
        var updatedAccountOverview = accountService.changePassword(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest body) {
        accountService.deleteAccount(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @ExceptionHandler(ValuesTheSameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleValuesTheSameException(ValuesTheSameException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(WrongPasswordException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseBody handlePasswordMismatchException(WrongPasswordException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.FORBIDDEN, req.getRequestURI(), e);
    }
}
