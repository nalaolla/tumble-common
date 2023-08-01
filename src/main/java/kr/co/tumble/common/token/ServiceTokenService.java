package kr.co.tumble.common.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * ServiceTokenService Class
 * api 서버간 restapi 호출 시 인증을 위한 토큰 서비스
 */
@Component
@Slf4j
public class ServiceTokenService implements TokenServiceForFilter {
	@Value("${jwt.service.key:defaultJwtServiceKeyWithEnoughLength}")
	private String jwtServiceKey;
	private static final String ALGORITHM = "HmacSHA256";
	private SecretKey secretKey;
	private static final String ISSUER = "tumble";
	private static final String SUBJECT = "tumble service token";

    private static final long TOKEN_VALID_MILISECOND = 1000L * 60; // 60초

    @PostConstruct
    protected void init() {
        secretKey = new SecretKeySpec(Base64.getEncoder().encode(jwtServiceKey.getBytes()), ALGORITHM);
    }

    public ServiceTokenDto createToken(TokenRequest tokenRequest) {
    	UserDetail userDetail = new UserDetail().setUserName(tokenRequest.getUserName());
		return new ServiceTokenDto().setToken(generateToken(userDetail, Arrays.asList("ROLE_SERVICE")));
    }
    
    public String generateToken(UserDetail userDetail,List<String> roles) {
    	final long now = System.currentTimeMillis();

        Claims claims = Jwts.claims();
        claims.put("userDetail", userDetail);
        claims.put("roles", roles);
        
		return Jwts.builder()
				.setClaims(claims)
			    .setSubject(SUBJECT)
			    .setIssuer(ISSUER)
			    .setIssuedAt(new Date(now))
			    .setNotBefore(new Date(now - TOKEN_VALID_MILISECOND))
			    .setExpiration(new Date(now + TOKEN_VALID_MILISECOND))
			    .signWith(secretKey)
			    .compact();
    }

    @Override
    public boolean verifyToken(String token) {
    	try {
    		final Jws<Claims> jws = parseToken(token);
    		
    		final String tokenSubject = jws.getBody().getSubject();
    		final String tokenIssuer = jws.getBody().getIssuer();
    		
    		if ( StringUtils.hasText(tokenSubject) && StringUtils.hasText(tokenIssuer) ) {
    			return tokenSubject.equals(SUBJECT) && tokenIssuer.equals(ISSUER);
    		}
		} catch (Exception e) {
			log.info("[COMMON][SERVICE_TOKEN_SERVICE] invalidate token: " + token);
		}
    	
        return false;
    }

    @Override
    public Jws<Claims> parseToken(String token) {
    	return Jwts.parserBuilder()
    			.setSigningKey(secretKey)
    			.build()
    			.parseClaimsJws(token);
    }
    
}
