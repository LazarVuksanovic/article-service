package rs.pravda.article_service.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.pravda.article_service.config.SecurityPropsConfig;
import rs.pravda.article_service.dto.auth.AuthRequest;
import rs.pravda.article_service.dto.auth.AuthResponse;
import rs.pravda.article_service.dto.auth.ChangePasswordRequest;
import rs.pravda.article_service.exception.RefreshTokenMissingException;
import rs.pravda.article_service.model.auth.Role;
import rs.pravda.article_service.model.auth.User;
import rs.pravda.article_service.repository.UserRepository;
import rs.pravda.article_service.service.AuthService;
import rs.pravda.article_service.service.JwtService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SecurityPropsConfig securityPropsConfig;

    public AuthResponse register(AuthRequest request, String firstName, String lastName) {
        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    public AuthResponse authenticate(AuthRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Set Refresh Token in HttpOnly Cookie
        var cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(securityPropsConfig.isSecureCookies());
        cookie.setPath("/");
        cookie.setMaxAge((int) securityPropsConfig.getRefreshExpiration());

        if (securityPropsConfig.getCookieDomain() != null) {
            cookie.setDomain(securityPropsConfig.getCookieDomain());
        }

        response.addCookie(cookie);

        return new AuthResponse(accessToken, user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null) throw new RefreshTokenMissingException();

        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                return new AuthResponse(accessToken, user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(securityPropsConfig.isSecureCookies());
        cookie.setPath("/");
        cookie.setMaxAge(0);

        if (securityPropsConfig.getCookieDomain() != null) {
            cookie.setDomain(securityPropsConfig.getCookieDomain());
        }

        response.addCookie(cookie);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}