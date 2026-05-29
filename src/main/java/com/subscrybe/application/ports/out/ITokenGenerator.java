package com.subscrybe.application.ports.out;

import com.subscrybe.domain.entities.User;

public interface ITokenGenerator {
    String generateToken(User user);
}