package com.musical.musican.Config;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.net.URLEncoder;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AccountRepository accountRepository;

    private final String[] PUBLIC_ENDPOINTS = {
            "/", "/Dangnhap", "/Dangky", "/api/auth/**",
            "/quenmk", "/datlaimk", "/checkotp", "/otpquenmk", "/datlaimatkhau", "/goilaiotp", "/doimatkhau",
            "/uploads/**", "/css/**", "/js/**", "/img/**", "/bootstrap-5.3.3/dist/**", "/fonts/**", "/logout", "/doimk"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/musician/**").hasAuthority("MUSICIAN")
                        .requestMatchers("/profile/**", "/favorite/**").hasAuthority("USER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/Dangnhap")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .failureHandler(customFailureHandler())
                        .successHandler((req, res, auth) -> {
                            Account currentUser = accountRepository.findByUsername(auth.getName())
                                    .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

                            req.getSession().setAttribute("currentUser", currentUser);
                            req.getSession().setAttribute("username",
                                    currentUser.getFullname() != null ? currentUser.getFullname()
                                            : currentUser.getEmail());
                            req.getSession().setAttribute("role", currentUser.getRole().name());

                            // Chuyển hướng dựa trên role
                            String redirectUrl = "/";
                            switch (currentUser.getRole()) {
                                case ADMIN -> redirectUrl = "/admin/dashboard";
                                case MUSICIAN -> redirectUrl = "/musican/dashboard";
                                case USER -> redirectUrl = "/";
                            }
                            res.sendRedirect(redirectUrl);
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

            if (Boolean.FALSE.equals(account.getActive())) {
                throw new DisabledException("Tài khoản chưa được kích hoạt");
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(account.getUsername())
                    .password(account.getPassword())
                    .authorities(account.getRole().name()) // Không thêm "ROLE_" nữa
                    .build();
        };
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            Optional<Account> accountOpt = accountRepository.findByUsername(username);

            if (accountOpt.isEmpty()) {
                response.sendRedirect("/Dangnhap?error=" + URLEncoder.encode("Tài khoản không tồn tại", "UTF-8"));
                return;
            }

            Account account = accountOpt.get();
            if (Boolean.FALSE.equals(account.getActive())) {
                response.sendRedirect("/Dangnhap?error=" + URLEncoder.encode("Tài khoản chưa được kích hoạt", "UTF-8"));
                return;
            }

            if (!bCryptPasswordEncoder().matches(password, account.getPassword())) {
                response.sendRedirect("/Dangnhap?error=" + URLEncoder.encode("Mật khẩu không chính xác", "UTF-8"));
                return;
            }

            response.sendRedirect("/Dangnhap?error=true");
        };
    }
}
