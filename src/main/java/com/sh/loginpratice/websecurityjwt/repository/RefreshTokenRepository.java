package com.sh.loginpratice.websecurityjwt.repository;

import com.sh.loginpratice.websecurityjwt.domain.RefreshToken;
import com.sh.loginpratice.websecurityjwt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
