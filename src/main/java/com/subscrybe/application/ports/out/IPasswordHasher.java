package com.subscrybe.application.ports.out;

public interface IPasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}