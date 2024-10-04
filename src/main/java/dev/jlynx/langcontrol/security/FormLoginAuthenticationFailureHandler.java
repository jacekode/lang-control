package dev.jlynx.langcontrol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.exception.ErrorResponseBody;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class FormLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FormLoginAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) throws IOException, ServletException {
        ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

        if (ex instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            var body = new ErrorResponseBody(
                    HttpStatus.UNAUTHORIZED,
                    request.getRequestURI(),
                    ex,
                    "Invalid username or password."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt with invalid username or password");
        } else if (ex instanceof LockedException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            var body = new ErrorResponseBody(
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    ex,
                    "Account is locked."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt to a locked account");
        } else if (ex instanceof DisabledException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            var body = new ErrorResponseBody(
                    HttpStatus.FORBIDDEN,
                    request.getRequestURI(),
                    ex,
                    "Account is disabled."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt to a disabled account");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            var body = new ErrorResponseBody(
                    HttpStatus.UNAUTHORIZED,
                    request.getRequestURI(),
                    ex,
                    "Login unsuccessful."
            );
            response.getWriter().write(JSON.writeValueAsString(body));
            LOG.debug("Login attempt unsuccessful");
        }

        response.setContentType("application/json");
        response.getWriter().flush();
    }
}
