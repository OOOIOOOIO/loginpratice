package com.sh.loginpratice.websecurityjwt.repository;

import com.sh.loginpratice.websecurityjwt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * username으로 User 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * username으로 User 존재하는지 확인
     */
    Boolean existsByUsername(String username);

    /**
     * email로 User 존재하는지 확인
     */
    Boolean existsByEmail(String email);
}
