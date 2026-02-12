package rs.pravda.article_service.service;

import jakarta.servlet.http.HttpServletResponse;
import rs.pravda.article_service.dto.auth.AuthRequest;
import rs.pravda.article_service.dto.auth.AuthResponse;
import rs.pravda.article_service.dto.auth.ChangePasswordRequest;

public interface AuthService {

    AuthResponse register(AuthRequest request, String firstName, String lastName);

    AuthResponse authenticate(AuthRequest request, HttpServletResponse response);

    AuthResponse refreshToken(String refreshToken);

    void logout(HttpServletResponse response);

    void changePassword(String email, ChangePasswordRequest request);
}
