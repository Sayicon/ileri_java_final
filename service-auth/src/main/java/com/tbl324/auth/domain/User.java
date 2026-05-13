package com.tbl324.auth.domain;

import java.time.LocalDateTime;

public final class User {

    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private final boolean active;

    private User(Builder b) {
        this.id           = b.id;
        this.username     = b.username;
        this.email        = b.email;
        this.passwordHash = b.passwordHash;
        this.role         = b.role;
        this.createdAt    = b.createdAt;
        this.active       = b.active;
    }

    public Long          getId()           { return id; }
    public String        getUsername()     { return username; }
    public String        getEmail()        { return email; }
    public String        getPasswordHash() { return passwordHash; }
    public UserRole      getRole()         { return role; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public boolean       isActive()        { return active; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long          id;
        private String        username;
        private String        email;
        private String        passwordHash;
        private UserRole      role;
        private LocalDateTime createdAt;
        private boolean       active = true;

        public Builder id(Long v)                 { this.id = v; return this; }
        public Builder username(String v)         { this.username = v; return this; }
        public Builder email(String v)            { this.email = v; return this; }
        public Builder passwordHash(String v)     { this.passwordHash = v; return this; }
        public Builder role(UserRole v)           { this.role = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder active(boolean v)          { this.active = v; return this; }
        public User build()                       { return new User(this); }
    }
}
