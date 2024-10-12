package dev.jlynx.langcontrol.auth;

import dev.jlynx.langcontrol.account.AccountService;
import dev.jlynx.langcontrol.account.dto.AccountOverviewResponse;
import dev.jlynx.langcontrol.auth.dto.RegisterRequestBody;
import dev.jlynx.langcontrol.jwtauth.dto.TokenResponseBody;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A rest controller which handles account registration requests. The intended use is while the form login auth
 * workflow is in action.
 */
@RestController
@RequestMapping("/{apiPref}/auth")
public class FormLoginController {

    private static final Logger LOG = LoggerFactory.getLogger(FormLoginController.class);

    private final AccountService accountService;

    @Autowired
    public FormLoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Handles the new account registration post request.
     *
     * @param body a DTO object which maps to the registration request's body
     */
    @PostMapping("/register")
    public ResponseEntity<AccountOverviewResponse> register(@Valid @RequestBody RegisterRequestBody body) {
        LOG.debug("register() method reached. body={}", body);
        var accountOverview = accountService.registerAccount(body);
        return new ResponseEntity<>(accountOverview, HttpStatus.CREATED);
    }
}
