package com.sh.loginpratice.websecurityjwt.controller.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
