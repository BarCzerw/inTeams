package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.dto.RegisterDTO;
import com.sda.inTeams.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping()
    public String getRegisterForm(Model model) {
        model.addAttribute("registerInfo", new RegisterDTO());
        return "register-new-team";
    }

    @PostMapping()
    public String registerTeam(RegisterDTO registerDTO) {
        try {
            Team registeredTeam = registerService.registerNewTeamWithUser(registerDTO);
            return "redirect:/team/" + registeredTeam.getId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "team/all";
        }
    }

}
