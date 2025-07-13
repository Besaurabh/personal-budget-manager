package com.pbm.config;

import com.pbm.service.CustomerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/register", "/login", "/css/**", "/images/**", "/js/**", "/").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                    .loginPage("/login")                      // custom login page
                    .defaultSuccessUrl("/dashboard", true)             // redirect here after successful login
                    .permitAll()
            .and()
                .logout()
                    .logoutUrl("/logout")                     // handled automatically by Spring
                    .logoutSuccessUrl("/login?logout")        // after logout, redirect to login with query param
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            .and()
                .csrf().disable(); // ❗ disable only during dev — enable it later for security
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                             BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
