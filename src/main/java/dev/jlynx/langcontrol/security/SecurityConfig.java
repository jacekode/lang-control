package dev.jlynx.langcontrol.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${rsa.private-key}")
    private RSAPrivateKey privateKey;

    @Value("${rsa.public-key}")
    private RSAPublicKey publicKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/styles/**", "/scripts/**", "/*.html", "/icons/**", "/*.svg").permitAll()
                        .requestMatchers("/admintools/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(new FormLoginAuthenticationSuccessHandler())
                        .failureHandler(new FormLoginAuthenticationFailureHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/login?logout")
                );
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    /**
     * Provides temporary in-memory users with user and admin roles for development purposes.
     * WARNING: Should not be used in production!
     *
     * @return an in-memory {@code UserDetailsService} implementation
     */
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

//    @Bean
//    JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
//    }
//
//    @Bean
//    JwtEncoder jwtEncoder() {
//        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
//        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
//        return new NimbusJwtEncoder(jwkSource);
//    }
//
//    @Bean
//    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
//        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
//        daoAuthProvider.setUserDetailsService(userDetailsService);
//        daoAuthProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(daoAuthProvider);
//    }
}
