package com.sh.loginpratice.rest_security_jwt_exception.repository;

import com.sh.loginpratice.rest_security_jwt_exception.domain.Authority;
import com.sh.loginpratice.rest_security_jwt_exception.domain.MemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority,String> {
    Optional<Authority> findByAuthorityName(MemberAuth authorityName);
}
