package com.aurionpro.bank.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aurionpro.bank.security.JwtAuthenticationEntryPoint;
import com.aurionpro.bank.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(request -> request
                .requestMatchers("/bank/register").permitAll()
                .requestMatchers("/bank/login").permitAll()
                .requestMatchers(HttpMethod.GET,"/bank/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/bank/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/bank/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/bank/admin/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET,"/bank/customer/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST,"/bank/customer/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT,"/bank/customer/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.DELETE,"/bank/customer/**").hasRole("CUSTOMER")
               
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
