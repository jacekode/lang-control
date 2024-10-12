package dev.jlynx.langcontrol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.security.dto.LoginSuccessResponseBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FormLoginAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

        var responseBody = new LoginSuccessResponseBody("Login successful");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(JSON.writeValueAsString(responseBody));
        response.getWriter().flush();
        LOG.debug("User login successful");
    }
}
