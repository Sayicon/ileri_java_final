package com.tbl324.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class RegisterRequest {

    @NotBlank
    private final String username;

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    @Size(min = 6)
    private final String password;

    @JsonCreator
    public RegisterRequest(
            @JsonProperty("username") String username,
            @JsonProperty("email")    String email,
            @JsonProperty("password") String password) {
        this.username = username;
        this.email    = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
