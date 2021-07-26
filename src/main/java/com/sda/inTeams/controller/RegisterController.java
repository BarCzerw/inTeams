package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.model.dto.RegisterTeamDTO;
import com.sda.inTeams.model.dto.RegisterUserDTO;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final RegisterService registerService;
    private final TeamRepository teamRepository;

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
    public String getUserRegisterForm(Model model, @RequestParam long teamId, @RequestParam String registerCode) {
        try {
            validateRegistrationCode(teamId, registerCode);
            RegisterUserDTO regDto = new RegisterUserDTO();
            regDto.setTeamId(teamId);
            regDto.setRegisterCode(registerCode);
            model.addAttribute("registerInfo", regDto);
            return "register-new-user";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/";
        }
    }

    private void validateRegistrationCode(long teamId, String registrationCode) throws InvalidOperation {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new InvalidOperation("Team not found"));
        if (!team.getUniqueRegisterId().equals(registrationCode)) {
            throw new InvalidOperation("Registration code does not match!");
        }
    }

    @PostMapping("/user")
    public String registerUser(RegisterUserDTO registerDTO) {
        try {
            User registeredUser = registerService.registerNewUserToExistingTeam(registerDTO);
            return "redirect:/team/" + registerDTO.getTeamId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/";
        }
    }

}
