package com.sh.loginpratice.websecurityjwt.controller.dto.request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String username;
    private String email;
    private String password;
    private Set<String> role;

}
