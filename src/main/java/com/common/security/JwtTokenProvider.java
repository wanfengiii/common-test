package com.common.security;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Log4j2
public class JwtTokenProvider {

    private static final String TYPE = "typ";

    private static final String TYPE_API = "api";

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.api.chinatelecom.secret}")
    private String chinatelecomSecretKey;

    @Value("${security.jwt.token.expire-length:3600000}") // 1h by default
    private long validityInMilliseconds;

    @Value("${security.enabled: true}")
    private boolean enabled;

    @Value("${security.runAs:}")
    private String runAs;

    @Autowired
    private MyUserDetailsService myUserDetails;

    @PostConstruct
    protected void init() {
        secretKey = encodeSecret(secretKey);
    }

    private String encodeSecret(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        String currentSecretKey = getCurrentSecretKey(token);
        Claims claims = Jwts.parser().setSigningKey(currentSecretKey).parseClaimsJws(token).getBody();
        String username = claims.getSubject();
        UserDetails userDetails = myUserDetails.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Claims getClaims(String token) {
        String currentSecretKey = getCurrentSecretKey(token);
        return Jwts.parser().setSigningKey(currentSecretKey).parseClaimsJws(token).getBody();
    }

    public String resolveToken(HttpServletRequest req) {
        if (enabled && StringUtils.isNotBlank(runAs)) {
            log.warn("security is disabled, all request are running as user {}", runAs);
            return generateRunAsToken();
        }
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private String generateRunAsToken() {
        return createToken(runAs);
    }

    public boolean validateToken(String token) throws BadCredentialsException {
        try {
            String currentSecretKey = getCurrentSecretKey(token);
            Jwts.parser().setSigningKey(currentSecretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredentialsExpiredException("Expired or invalid JWT token");
        }
    }

    private Claims parseClaims(String token) {
        JwtTokenParser p = new JwtTokenParser();
        Jwt jwt = p.parse(token);
        Object body = jwt.getBody();
        if (!(body instanceof Claims)) {
            log.error("token body parse failed, parse result is not type of Claims, is {} actually", body.getClass().getName());
            return null;
        }
        return (Claims) body;
    }

    /**
     * get the secretKey for *THIS* token
     */
    private String getCurrentSecretKey(String token) {
        Claims claims = parseClaims(token);
        boolean isApi = TYPE_API.equals(claims.get(TYPE));
        String username = claims.getSubject();
        if (StringUtils.isBlank(username) || !isApi) {
            // pass to the constant secretKey process chain
            return secretKey;
        }

        return getSecretKey4User(username);
    }

    /**
     * load secret key based on username
     * TODO Ike, a temp implementation here
     */
    private String getSecretKey4User(String username) {
        return encodeSecret(chinatelecomSecretKey);
    }

    /**
     * sample code for dev doc
     */
    // public static void main(String[] args) throws Exception {
    //     String username = "api_test_user";
    //     String secret = "test_user_secret";

    //     // 有效期 1 个小时, 在过期前, 需重新生成, 避免 api 认证失败
    //     long validityInMilliseconds = 3600000;

    //     // 设置用户名
    //     Claims claims = Jwts.claims().setSubject(username); 

    //     // 设置为 api 调用 token, 必须设置
    //     claims.put("typ", "api");   

    //     Date now = new Date();
    //     Date validity = new Date(now.getTime() + validityInMilliseconds);

    //     // secret base64 encoded
    //     secret = Base64.getEncoder().encodeToString(secret.getBytes());

    //     String token = Jwts.builder()
    //         // 设置 claims
    //         .setClaims(claims)
    //         // 设置签发时间
    //         .setIssuedAt(now)
    //         // 设置过期时间
    //         .setExpiration(validity)    
    //         // 设置签名算法和 secret
    //         .signWith(SignatureAlgorithm.HS256, secret) 
    //         .compact();

    //     System.out.println("token:" + token);
    // }

}
