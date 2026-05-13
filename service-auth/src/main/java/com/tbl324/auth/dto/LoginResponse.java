package com.tbl324.auth.dto;

public final class LoginResponse {

    private final String token;
    private final Long   userId;
    private final String username;
    private final String role;

    public LoginResponse(String token, Long userId, String username, String role) {
        this.token    = token;
        this.userId   = userId;
        this.username = username;
        this.role     = role;
    }

    public String getToken()    { return token; }
    public Long   getUserId()   { return userId; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }
}
