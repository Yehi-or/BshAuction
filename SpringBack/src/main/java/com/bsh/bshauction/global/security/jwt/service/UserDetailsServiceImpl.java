package com.bsh.bshauction.global.security.jwt.service;

import com.bsh.bshauction.entity.User;
import com.bsh.bshauction.entity.UserRole;
import com.bsh.bshauction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        List<UserRole> userRoles = user.get().getRole();

        List<GrantedAuthority> authorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        log.info(authorities.toString());

        return new org.springframework.security.core.userdetails.User(
                user.get().getUserEmail(),
                user.get().getPassword(),
                authorities
        );
    }
}
