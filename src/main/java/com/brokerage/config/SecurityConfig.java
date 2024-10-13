package com.brokerage.config;

import com.brokerage.auth.JwtAuthFilter;
import com.brokerage.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        // @todo remove h2 console for prod
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(antMatcher("/swagger-ui/**"),
                                    antMatcher("/swagger-ui.html"),
                                    antMatcher("/api/auth/register/**"),
                                    antMatcher("/api/auth/login/**"),
                                    antMatcher("/v3/**"),
                                    antMatcher("/h2-console/**")).permitAll()
                            .anyRequest().authenticated();
                })
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }



}

// CORS(Cross-origin resource sharing) is just to avoid if you run javascript across different domains like if you execute JS on http://testpage.com and access http://anotherpage.com
// CSRF(Cross-Site Request Forgery)
