//package dev.jlynx.langcontrol.jwtauth;
//
//import dev.jlynx.langcontrol.jwtauth.dto.LoginRequestBody;
//import dev.jlynx.langcontrol.jwtauth.dto.RegisterRequestBody;
//import dev.jlynx.langcontrol.jwtauth.dto.TokenResponseBody;
//import dev.jlynx.langcontrol.security.TokenService;
//import dev.jlynx.langcontrol.account.AccountService;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//
///**
// * A rest controller which handles login and account registration requests. Should be used only when the JWT token
// * authentication workflow is in action.
// */
//@RestController
//@RequestMapping("/{apiPref}/auth")
//public class JwtAuthController {
//
//    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthController.class);
//
//    private final AuthenticationManager authenticationManager;
//    private final TokenService tokenService;
//    private final AccountService accountService;
//
//    @Autowired
//    public JwtAuthController(
//            AuthenticationManager authenticationManager,
//            TokenService tokenService,
//            AccountService accountService
//    ) {
//        this.authenticationManager = authenticationManager;
//        this.tokenService = tokenService;
//        this.accountService = accountService;
//    }
//
//    /**
//     * Handles the user login post request.
//     *
//     * @param body a DTO object which maps to the login request's body
//     * @return a JWT token used by the client for further authenticated requests
//     */
//    @PostMapping("/login")
//    public TokenResponseBody login(@Valid @RequestBody LoginRequestBody body) {
//        LOG.debug("login() method reached. body={}", body);
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(body.username(), body.password())
//        );
//        return new TokenResponseBody(tokenService.generateToken(authentication));
//    }
//
//    /**
//     * Handles the new account registration post request.
//     *
//     * @param body a DTO object which maps to the registration request's body
//     * @return a JWT token used by the client for further requests' authentication
//     */
//    @PostMapping("/register")
//    public TokenResponseBody register(@Valid @RequestBody RegisterRequestBody body) {
//        LOG.debug("register() method reached. body={}", body);
//        var account = accountService.registerAccount(body);
//        String token = tokenService.generateToken(
//                new UsernamePasswordAuthenticationToken(body.username(), body.password())
//        );
//        return new TokenResponseBody(token);
//    }
//}
