package kr.co.tumble.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * AuthUtil
 */
@Slf4j
public class AuthUtil {

	private AuthUtil(){}

    public static Optional<String> resolveToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (StringUtils.hasText(authorization) && StringUtils.startsWithIgnoreCase(authorization, "Bearer")) {
			return Optional.ofNullable(authorization.substring("Bearer".length()));
		}
		return Optional.empty();
    }

    /**
     * pathInfo의 LangCd 조회
     * @param request
     * @return
     */
    public static Optional<String> resolveLangCd(HttpServletRequest request) {
		String pathInfo = request.getServletPath();
		log.debug("pathInfo: {}", pathInfo);
		log.debug("getRequestURI: {}", request.getRequestURI());
		
		try {
			/* 언어구분 체크시, 한/영 구분이 없는 URL 예외 처리 진행 - 대표사용처 : 더한섬닷컴 구주문보기 */
            if (request.getRequestURI().indexOf("/api/common/member/checkToken") >= 0) {
				return Optional.of("world");
			} else {
				if (StringUtils.hasLength(pathInfo)) {
					String[] pathVars = StringUtils.tokenizeToStringArray(pathInfo, "/");
					if (pathVars.length >= 2) {
						return Optional.of(pathVars[1]);
					}
				} else {
					return Optional.empty();
				}
			}
		} catch (Exception e) {
			return Optional.empty();
		}
		
		return Optional.empty();
    }

}