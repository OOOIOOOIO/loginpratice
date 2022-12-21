package com.sh.loginpratice.restsecurityjwt.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
}
