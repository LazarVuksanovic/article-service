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
import rs.pravda.article_service.dto.auth.*;
import rs.pravda.article_service.exception.AuthenticationException;
import rs.pravda.article_service.exception.RefreshTokenMissingException;
import rs.pravda.article_service.model.auth.Provider;
import rs.pravda.article_service.model.auth.Role;
import rs.pravda.article_service.model.auth.User;
import rs.pravda.article_service.repository.UserRepository;
import rs.pravda.article_service.service.AuthService;
import rs.pravda.article_service.service.JwtService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SecurityPropsConfig securityPropsConfig;

    @Override
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.findByEmail(request.email()).isEmpty()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        setRefreshCookie(response, refreshToken);

        return new AuthResponse(accessToken, user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    @Override
    public AuthResponse authenticate(AuthRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        setRefreshCookie(response, refreshToken);

        return new AuthResponse(accessToken, user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    @Override
    public AuthResponse googleAuth(GoogleAuthRequest request, HttpServletResponse response) {
        GoogleIdToken.Payload payload = verifyGoogleToken(request.credential());

        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        if (firstName == null) firstName = email.split("@")[0];
        if (lastName == null) lastName = "";

        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        var user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    var newUser = User.builder()
                            .email(email)
                            .firstName(finalFirstName)
                            .lastName(finalLastName)
                            .provider(Provider.GOOGLE)
                            .password(null)
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        // 3. Issue our own JWT pair
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        setRefreshCookie(response, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleToken(String credential) {
        try {
            var verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(securityPropsConfig.getGoogleClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null) {
                throw new IllegalArgumentException("Invalid Google token.");
            }
            return idToken.getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Google token verification failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null) throw new RefreshTokenMissingException();

        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new AuthenticationException("Invalid refresh token") {};
        }

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new org.springframework.security.core.AuthenticationException("Refresh token expired or invalid") {};
        }

        var accessToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public void logout(HttpServletResponse response) {
        var cookie = new Cookie("refresh_token", null);
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

        if (!Provider.LOCAL.equals(user.getProvider())){
            throw new IllegalArgumentException("Password change is not allowed for accounts registered via " + user.getProvider());
        }

        if (user.getPassword() == null) {
            throw new IllegalArgumentException("This account uses Google sign-in and has no password.");
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        var cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(securityPropsConfig.isSecureCookies());
        cookie.setPath("/");
        cookie.setMaxAge((int) (securityPropsConfig.getRefreshExpiration() / 1000));
        if (securityPropsConfig.getCookieDomain() != null) {
            cookie.setDomain(securityPropsConfig.getCookieDomain());
        }
        response.addCookie(cookie);
    }
}