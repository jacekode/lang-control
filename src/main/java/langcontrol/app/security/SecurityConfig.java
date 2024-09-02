package langcontrol.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/signin", "/signup", "/").permitAll()
                .requestMatchers("/css/**", "/js/**").permitAll()
                .requestMatchers("/admintools/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated());

        http.formLogin(login -> login
                        .loginPage("/signin"));
        http.logout(logout -> logout
                .logoutUrl("/signout"));
        return http.build();
    }

//    @Bean
//    public UserDetailsService inMemoryUsers() {
//        UserDetails user = User.builder()
//                .username("user")
//                .password("$2a$10$cByk53c5OqPtHyrzMF23FeXc7weAMcmV9OIWrFvYXWhoJGl2LmO8m") // password: user
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("$2a$10$dAaUzoiUTI3ObIL.qZeRdeWcrT01gJXsZKsB6yBfd.Y2R9tPB6fN6") // password: admin
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
