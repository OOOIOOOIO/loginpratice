package com.sh.loginpratice.rest_security_jwt_exception.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Member들의 권한정보 entity
 */
@Entity
@Table(name = "authority")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    @Column(name = "authority_name",length = 50)
    @Enumerated(EnumType.STRING)
    private MemberAuth authorityName;

    public String getAuthorityName() {
        return this.authorityName.toString();
    }
}