package com.sh.loginpratice.restsecurityjwt.repository;

import com.sh.loginpratice.restsecurityjwt.domain.RefreshToken;
import com.sh.loginpratice.restsecurityjwt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
