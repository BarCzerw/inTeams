package com.sda.inTeams.controller;

import com.sda.inTeams.repository.AccountRoleRepository;
import com.sda.inTeams.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AuthorizationService authorizationService;
    private final AccountRoleRepository accountRoleRepository;

    @GetMapping()
    public String getAdminPanel(Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            return "admin-panel";
        } else {
            return "redirect:/";
        }
    }
}
