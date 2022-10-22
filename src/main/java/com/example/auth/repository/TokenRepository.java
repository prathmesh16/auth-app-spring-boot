package com.example.auth.repository;

import com.example.auth.model.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Token findByToken(String token);
}
