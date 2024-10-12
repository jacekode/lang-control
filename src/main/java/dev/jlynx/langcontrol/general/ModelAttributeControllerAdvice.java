package dev.jlynx.langcontrol.general;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// todo: should i remove this class?
@ControllerAdvice
public class ModelAttributeControllerAdvice {

    @ModelAttribute("requestUri")
    public String requestUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
