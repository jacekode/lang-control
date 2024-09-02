package langcontrol.app.security;

import jakarta.validation.Valid;
import langcontrol.app.account.AccountRegistrationDTO;
import langcontrol.app.account.AccountService;
import langcontrol.app.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/signin")
    public String getSignInPage() {
        return "signin";
    }

    @GetMapping("/signup")
    public String getSignUpPage(Model model) {
        model.addAttribute("registrationData", new AccountRegistrationDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerAccount(@ModelAttribute("registrationData")
                                      @Valid AccountRegistrationDTO registrationDto) {
        accountService.registerNewUserAccount(registrationDto);
        return "redirect:?accountCreated";
    }


    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ModelAndView handleUsernameAlreadyExistsException() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("usernameTaken", true);
        mav.setViewName("signup");
        return mav;
    }
}
