package com.sh.loginpratice.rest_security_jwt_exception.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * refresh token entity
 * RDBMS로 관리(Redis가 일반적이지만 일단 RDBBS로)
 */
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @Id
    private String key;

    @Column(nullable = false)
    private String value;

    public void updateValue(String token) {
        this.value = token;
    }

    @Builder
    public RefreshToken(String key, String value) {
        this.key = key;
        this.value = value;
    }
}