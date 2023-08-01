package kr.co.tumble.common.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * TokenServiceForFilter interface
 */
public interface TokenServiceForFilter {
    boolean verifyToken(String token) ;
    Jws<Claims> parseToken(String token) ;
}
