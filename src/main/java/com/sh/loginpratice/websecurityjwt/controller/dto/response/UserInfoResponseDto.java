package com.sh.loginpratice.websecurityjwt.controller.dto.response;

import lombok.*;

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
