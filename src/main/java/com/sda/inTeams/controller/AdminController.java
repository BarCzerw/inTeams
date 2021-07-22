package com.sda.inTeams.controller;

import com.sda.inTeams.model.User.AccountRole;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.AccountRoleRepository;
import com.sda.inTeams.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AuthorizationService authorizationService;
    private final AccountRoleRepository accountRoleRepository;

    @GetMapping()
    public String getAdminPanel(Principal principal) {
        Optional<User> userOptional = authorizationService.checkUserCredentials(principal);
        if (userOptional.isPresent()) {
            AccountRole accountRole = accountRoleRepository.findByName("ROLE_ADMIN").get();
            User user = userOptional.get();
            if (user.getRoles().contains(accountRole)) {
                return "admin-panel";
            }
        }
        return "redirect:/";
    }

}
