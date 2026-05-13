package com.tbl324.auth.service;

import com.tbl324.auth.domain.User;
import com.tbl324.auth.domain.UserRole;
import com.tbl324.auth.dto.LoginRequest;
import com.tbl324.auth.dto.LoginResponse;
import com.tbl324.auth.dto.RegisterRequest;
import com.tbl324.auth.repository.UserJdbcRepository;
import com.tbl324.auth.security.PasswordHasher;
import com.tbl324.auth.security.TokenService;
import com.tbl324.shared.exception.ConflictException;
import com.tbl324.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserJdbcRepository userRepo;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final SessionRedisRepository sessionRepo;

    public AuthService(UserJdbcRepository userRepo,
                       PasswordHasher passwordHasher,
                       TokenService tokenService,
                       SessionRedisRepository sessionRepo) {
        this.userRepo       = userRepo;
        this.passwordHasher = passwordHasher;
        this.tokenService   = tokenService;
        this.sessionRepo    = sessionRepo;
    }

    public LoginResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ConflictException("Bu kullanıcı adı zaten alınmış");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ConflictException("Bu e-posta zaten kayıtlı");
        }
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordHasher.hash(req.getPassword()))
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
        User saved = userRepo.save(user);
        return buildLoginResponse(saved);
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Geçersiz kullanıcı adı veya şifre"));
        if (!user.isActive()) {
            throw new UnauthorizedException("Hesap devre dışı");
        }
        if (!passwordHasher.verify(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Geçersiz kullanıcı adı veya şifre");
        }
        return buildLoginResponse(user);
    }

    public void logout(String sessionId) {
        sessionRepo.revokeSession(sessionId);
    }

    private LoginResponse buildLoginResponse(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessionRepo.saveSession(sessionId, user.getId());
        String token = tokenService.generateToken(
                user.getId(), user.getUsername(), user.getRole().name(), sessionId);
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
