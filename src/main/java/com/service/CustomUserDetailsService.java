package com.service;

import com.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Change this code to load from DB
        return new User(username, passwordEncoder.encode("1"), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));
    }
}
