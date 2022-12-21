package com.sh.loginpratice.rest_security_jwt_exception.repository;

import com.sh.loginpratice.rest_security_jwt_exception.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
