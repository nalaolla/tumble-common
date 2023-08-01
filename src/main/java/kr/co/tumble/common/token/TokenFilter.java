package kr.co.tumble.common.token;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.tumble.common.context.CookieContextHolder;
import kr.co.tumble.common.exception.InvalidTokenException;
import kr.co.tumble.common.util.AuthUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TokenFilter
 * api 서버간 restapi 호출 시 인증을 위한 토큰 필터
 */
public class TokenFilter extends GenericFilterBean {
	private TokenServiceForFilter tokenService;

	public TokenFilter(TokenServiceForFilter tokenService) {
		this.tokenService = tokenService;
	}
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		Optional<String> jwtOpt = AuthUtil.resolveToken((HttpServletRequest)request);
		try {
			if (jwtOpt.isPresent() && tokenService.verifyToken(jwtOpt.get())) {
				Jws<Claims> jws = tokenService.parseToken(jwtOpt.get());
				String mbrNo = (String)jws.getBody().get("mbrNo");
				String langCd = Optional.ofNullable((String)jws.getBody().get("langCd")).orElse("");
				UserDetail userDetail;
				if (StringUtils.isNotBlank(mbrNo)) {

					// 로그인한 사용자의 langCd값과 cookie상의 설정한 langCd값이 다를 경우 권한 Exception 발생
					String cookieLangCd = CookieContextHolder.getCookieMap().getMap().get("lang_cd");
					if(!cookieLangCd.equals(langCd)) {
						throw new InvalidTokenException("0403", "User langCd and cookie langCd are different.");
					}
					
					userDetail = ((MemberTokenService)tokenService).getUserDetail(mbrNo, langCd);
				} else {
					userDetail = new UserDetail().setMbrNo(mbrNo);
				}
				List<String> roles = (List<String>)jws.getBody().get("roles");
				List<GrantedAuthority> authorities = new ArrayList<>();
				if (CollectionUtils.isNotEmpty(roles)) {
					roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
				}
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, "", authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch(ExpiredJwtException e) {
			String responseJson = "{\"code\":\"0403\", \"message\":\"FORBIDDEN: Jwt Expired.\"}";
			((HttpServletResponse)response).setStatus(HttpStatus.FORBIDDEN.value());
			response.getOutputStream().print(responseJson);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			return;
		}

		chain.doFilter(request, response);
    }
    
}
