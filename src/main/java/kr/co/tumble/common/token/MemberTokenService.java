package kr.co.tumble.common.token;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import kr.co.tumble.common.context.CookieContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * MemberTokenService Class
 */
@Service
@Lazy
@Slf4j
@RequiredArgsConstructor
public class MemberTokenService implements TokenServiceForFilter {
	@Value("${jwt.member.access.key:defaultJwtMemberAccessKeyWithEnoughLength}")
	private String accessKey;
	@Value("${jwt.member.refresh.key:defaultJwtMemberRefreshKeyWithEnoughLength}")
	private String refreshKey;
	@Value("${jwt.member.rememberme.key:${jwt.member.refresh.key:defaultJwtMemberRemembermeKeyWithEnoughLength}}")
	private String remembermeKey;
	@Value("${jwt.member.biometric.key:${jwt.member.refresh.key:defaultJwtMemberBiometicKeyWithEnoughLength}}")
	private String biometricKey;
	@Value("${app.lang.defaultSystemLanguage?:'ko'}")
	private String defaultSystemLanguage;

	private static final String ALGORITHM = "HmacSHA256";

	private SecretKey accessSecretKey;
	private SecretKey refreshSecretKey;
	private SecretKey remembermeSecretKey;
	private SecretKey biometricSecretKey;
	
	private final UserDetailService userDetailService;

    @PostConstruct
    protected void init() {
    	accessSecretKey = new SecretKeySpec(Base64.getEncoder().encode(accessKey.getBytes()), ALGORITHM);
    	refreshSecretKey = new SecretKeySpec(Base64.getEncoder().encode(refreshKey.getBytes()), ALGORITHM);
    	remembermeSecretKey = new SecretKeySpec(Base64.getEncoder().encode(remembermeKey.getBytes()), ALGORITHM);
    	biometricSecretKey = new SecretKeySpec(Base64.getEncoder().encode(biometricKey.getBytes()), ALGORITHM);
    }

	// access token 유효시간: 120분
	private static final Integer ACCESS_EXP_MIN = 120;
	// refresh token 유효시간: 3시간
	private static final Integer REFRESH_EXP_MIN = 60*3;
	// 자동로그인 token 유효시간: 10년
	private static final Integer REMEMBERME_EXP_MIN = 60*24*365*10;
	// 생체인증 token 유효시간: 10년
	private static final Integer BIOMETRIC_EXP_MIN = 60*24*365*10;

	private static final String ISSUER = "x2bee";
	private static final String SUBJECT = "x2bee member token";

	public MemberTokenDto create(UserDetail userInfo) {
		// Create Access Token
		String accessToken = createJws(accessSecretKey, ACCESS_EXP_MIN, userInfo);

		// Create Refresh Token
		String refreshToken = createJws(refreshSecretKey, REFRESH_EXP_MIN, null);

		MemberTokenDto tokens = new MemberTokenDto();
		tokens.setAccessToken(accessToken);
		tokens.setRefreshToken(refreshToken);
		tokens.setMbrNo(userInfo.getMbrNo());

		return tokens;

	}

	/**
	 * API 인증용 임시토큰 발급
	 * @param userInfo
	 * @param accessExpMin
	 * @return
	 */
	public String createRestApiTempAccessToken(UserDetail userInfo, Integer accessExpMin) {
		return createJws(accessSecretKey, accessExpMin, userInfo);
	}

	private String createJws(SecretKey secretKey, Integer expMin, UserDetail userDetail) {
		//JWT Builder create
		JwtBuilder builder = Jwts.builder();

		// header configuration
		builder.setHeaderParam("typ", "JWT");

		// claim configuration
		builder.setIssuer(ISSUER);
		builder.setSubject(SUBJECT);
		builder.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expMin));
		builder.setIssuedAt(new Date());
		builder.setId(UUID.randomUUID().toString());
		if(userDetail != null) {
			builder.claim("mbrNo", userDetail.getMbrNo());

			String langCd = CookieContextHolder.getCookieMap().getMap().getOrDefault("lang_cd", defaultSystemLanguage);
			builder.claim("langCd", langCd);

			builder.claim("roles", Arrays.asList("ROLE_MEMBER"));
		}

		// signature configuration
		builder.signWith(secretKey);
		String jws = builder.compact();

		return jws;

	}

	/**
	 * 자동로그인 토큰 생성
	 * @return
	 */
	public String createRemembermeToken() {
		return createJws(remembermeSecretKey, REMEMBERME_EXP_MIN, null);
	}

	/**
	 * 생체인증 토큰 생성
	 * @return
	 */
	public String createBiometricToken() {
		return createJws(biometricSecretKey, BIOMETRIC_EXP_MIN, null);
	}
	
	/**
	 * 리프레시 토큰 유효성 검증
	 * @param token
	 * @return
	 */
	public boolean verifyRefreshToken(String token) {
		return verifyEmptyToken(token, refreshSecretKey) ;
	}

	/**
	 * 자동로그인 토큰 유효성 검증
	 * @param token
	 * @return
	 */
	public boolean verifyRemembermeToken(String token) {
		return verifyEmptyToken(token, remembermeSecretKey) ;
	}

	/**
	 * 생체인증 토큰 유효성 검증
	 * @param token
	 * @return
	 */
	public boolean verifyBiometricToken(String token) {
		return verifyEmptyToken(token, biometricSecretKey) ;
	}

	/**
	 * 리프레시/자동로그인/생체로그인 토큰 유효성 검증
	 * @param token
	 * @return
	 */
	public boolean verifyEmptyToken(String token, SecretKey secretKey) {
		try {
			Jwts.parserBuilder()
    			.setSigningKey(secretKey)
    			.build()
    			.parseClaimsJws(token);
    	} catch (ExpiredJwtException e) {
			log.info("[COMMON][MEMBER_TOKEN_SERVICE] ExpiredJwtException: " + token);
    		throw e;
		} catch (Exception e) {
			log.error("[COMMON][MEMBER_TOKEN_SERVICE] invalid token: " + token, e);
			return false;
		}
		return true;
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
    	} catch (ExpiredJwtException e) {
			log.info("[COMMON][MEMBER_TOKEN_SERVICE] ExpiredJwtException: " + token);
    		throw e;
		} catch (Exception e) {
			log.info("[COMMON][MEMBER_TOKEN_SERVICE] invalid token: " + token);
		}

        return false;
	}

	@Override
	public Jws<Claims> parseToken(String token) {
    	return Jwts.parserBuilder()
    			.setSigningKey(accessSecretKey)
    			.build()
    			.parseClaimsJws(token);
	}
	
	public UserDetail getUserDetail(String mbrNo, String langCd) {
		return userDetailService.getUserDetail(mbrNo, langCd);
	}
	
}
