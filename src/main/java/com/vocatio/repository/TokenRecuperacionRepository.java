package com.vocatio.repository;

import com.vocatio.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
    Optional<TokenRecuperacion> findByToken(String token);
    void deleteAllByExpiracionBefore(LocalDateTime expiracion);
}
