package uammonitoring.server.Config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uammonitoring.server.error.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtValidator {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims validateAccessToken(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (SecurityException e) {
            log.error("잘못된 시그니처");
            throw new JwtException(ErrorCode.JWT_INVALID.name());
        } catch (MalformedJwtException e) {
            log.error("유효하지 않은 JWT 토큰");
            throw new JwtException(ErrorCode.JWT_INVALID.name());
        } catch (ExpiredJwtException e) {
            log.error("Jwt 만료");
            throw new JwtException(ErrorCode.JWT_EXPIRED.name());
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰 형식");
            throw new JwtException(ErrorCode.JWT_INVALID.name());
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid.");
            throw new JwtException(ErrorCode.JWT_INVALID.name());
        }
    }
}
