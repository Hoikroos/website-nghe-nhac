package com.musical.musican.Security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.musical.musican.Model.Entity.Account;
import com.musical.musican.Repository.AccountRepository;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new UsernameNotFoundException("Tên đăng nhập không được để trống");
        }
        Account account = null;
        account = accountRepository.findByEmail(identifier).orElse(null);
        if (account == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với thông tin: " + identifier);
        }
        if (!account.getActive()) {
            throw new UsernameNotFoundException("Tài khoản chưa được kích hoạt");
        }
        return new CustomUserDetails(account);

    }
}
