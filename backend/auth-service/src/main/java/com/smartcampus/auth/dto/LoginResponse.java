package com.smartcampus.auth.dto;

import com.smartcampus.auth.entity.Role;

import java.util.UUID;

public record LoginResponse(

        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        String accessToken,
        String tokenType,
        long expiresIn

) {
}