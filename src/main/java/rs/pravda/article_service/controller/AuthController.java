package rs.pravda.article_service.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.pravda.article_service.dto.auth.AuthRequest;
import rs.pravda.article_service.dto.auth.AuthResponse;
import rs.pravda.article_service.dto.auth.ChangePasswordRequest;
import rs.pravda.article_service.service.AuthService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody AuthRequest request,
            HttpServletResponse response
    ) {
        return authService.authenticate(request, response);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        authService.changePassword(principal.getName(), request);
    }
}