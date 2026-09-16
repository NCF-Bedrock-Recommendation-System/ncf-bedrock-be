package com.example.base.repository;

import com.example.base.domain.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

  void deleteByExpiryTimeBefore(LocalDateTime expiryTimeBefore);

  boolean existsById(String id);

}
