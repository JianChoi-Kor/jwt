package com.example.jwt.repository;

import com.example.jwt.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
}
