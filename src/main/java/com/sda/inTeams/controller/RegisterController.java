package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.model.dto.RegisterTeamDTO;
import com.sda.inTeams.model.dto.RegisterUserDTO;
import com.sda.inTeams.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/team")
    public String getTeamRegisterForm(Model model) {
        model.addAttribute("registerInfo", new RegisterTeamDTO());
        return "register-new-team";
    }

    @PostMapping("/team")
    public String registerTeam(RegisterTeamDTO registerDTO) {
        try {
            Team registeredTeam = registerService.registerNewTeamWithUser(registerDTO);
            return "redirect:/team/" + registeredTeam.getId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "team/all";
        }
    }

    @GetMapping("/user")
    public String getUserRegisterForm(Model model) {
        model.addAttribute("registerInfo", new RegisterUserDTO());
        model.addAttribute("teamList", registerService.getAllAvailableTeams());
        return "register-new-user";
    }

    @PostMapping("/user")
    public String registerUser(RegisterUserDTO registerDTO) {
        try {
            User registeredUser = registerService.registerNewUserToExistingTeam(registerDTO);
            return "redirect:/team/" + registerDTO.getTeamId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "team/all";
        }
    }

}
