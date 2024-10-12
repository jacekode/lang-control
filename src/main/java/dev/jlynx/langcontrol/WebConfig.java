package dev.jlynx.langcontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {

//    @Bean
//    public InternalResourceViewResolver internalResourceViewResolver() {
//        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
//        internalResourceViewResolver.setPrefix("static/");
//        internalResourceViewResolver.setSuffix(".html");
//        return internalResourceViewResolver;
//    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/register").setViewName("forward:/register.html");
                registry.addViewController("/login").setViewName("forward:/login.html");
                registry.addViewController("/create/card").setViewName("forward:/create-card.html");
                registry.addViewController("/create/deck").setViewName("forward:/create-deck.html");
                registry.addViewController("/browse").setViewName("forward:/browse.html");
                registry.addViewController("/review").setViewName("forward:/review.html");
                registry.addViewController("/decks/*/edit").setViewName("forward:/edit-deck.html");
                registry.addViewController("/cards/*/edit").setViewName("forward:/edit-card.html");
                registry.addViewController("/settings/app").setViewName("forward:/settings-app.html");
                registry.addViewController("/settings/account").setViewName("forward:/settings-account.html");
            }
        };
    }
}
