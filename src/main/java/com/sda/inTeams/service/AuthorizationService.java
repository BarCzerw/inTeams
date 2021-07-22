package com.sda.inTeams.service;

import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> accountOptional = userRepository.findByUsername(username);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        }
        throw new  UsernameNotFoundException("Cannot find username:" + username);
    }

    public Optional<User> checkUserCredentials(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
            if (usernamePasswordAuthenticationToken.getPrincipal() instanceof User) {
                return  Optional.of((User) usernamePasswordAuthenticationToken.getPrincipal());
            }
        }
        return Optional.empty();
    }
}
