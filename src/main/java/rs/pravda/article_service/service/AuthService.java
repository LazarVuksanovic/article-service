package rs.pravda.article_service.service;

import jakarta.servlet.http.HttpServletResponse;
import rs.pravda.article_service.dto.auth.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request, HttpServletResponse response);

    AuthResponse authenticate(AuthRequest request, HttpServletResponse response);

    AuthResponse refreshToken(String refreshToken);

    void logout(HttpServletResponse response);

    void changePassword(String email, ChangePasswordRequest request);

    AuthResponse googleAuth(GoogleAuthRequest request, HttpServletResponse response);
}
