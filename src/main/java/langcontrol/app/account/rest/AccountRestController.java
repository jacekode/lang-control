package langcontrol.app.account.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import langcontrol.app.account.AccountService;
import langcontrol.app.exception.ErrorResponseBody;
import langcontrol.app.exception.UsernamesTheSameException;
import langcontrol.app.exception.PasswordMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/api/account")
@RestController
public class AccountRestController {

    private final AccountService accountService;

    @Autowired
    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("/username")
    public ResponseEntity<Object> updateUsername(@Valid @ModelAttribute UpdateUsernameDTO dto) {
        AccountOverviewDTO accountOverview = accountService.updateUsername(dto);
        return ResponseEntity.ok(accountOverview);
    }

    @PutMapping("/password")
    public ResponseEntity<Object> updatePassword(@Valid @ModelAttribute UpdatePasswordDTO dto) {
        AccountOverviewDTO accountOverview = accountService.updatePassword(dto);
        return ResponseEntity.ok(accountOverview);
    }

    @PostMapping("/delete")
    public ResponseEntity<Object> deleteAccount(@Valid @ModelAttribute DeleteAccountDTO dto) {
        accountService.deleteAccount(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ExceptionHandler(UsernamesTheSameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponseBody handleUsernamesTheSameException(UsernamesTheSameException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponseBody handleWrongPasswordException(PasswordMismatchException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.FORBIDDEN, req.getRequestURI(), e);
    }
}
