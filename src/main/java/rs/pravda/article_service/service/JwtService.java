package rs.pravda.article_service.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtService {

    public String generateToken(UserDetails userDetails);

    public String generateRefreshToken(UserDetails userDetails);

    public boolean isTokenValid(String token, UserDetails userDetails);

    public String extractUsername(String token);

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
