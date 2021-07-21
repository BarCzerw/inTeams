package com.sda.inTeams.model.dto;

import lombok.Data;

@Data
public class RegisterUserDTO implements RegisterDto {

    private long teamId;

    private String username;
    private String password;
    private String confirmPassword;

}
