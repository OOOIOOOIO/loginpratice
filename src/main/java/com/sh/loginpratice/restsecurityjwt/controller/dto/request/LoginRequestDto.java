package com.sh.loginpratice.restsecurityjwt.controller.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
