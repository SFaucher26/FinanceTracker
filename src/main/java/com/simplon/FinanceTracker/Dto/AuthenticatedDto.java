package com.simplon.FinanceTracker.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthenticatedDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;

    public AuthenticatedDto(String accessToken, Long userId, String username) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
    }
}
