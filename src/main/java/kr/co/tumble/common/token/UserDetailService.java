package kr.co.tumble.common.token;


import kr.co.tumble.common.exception.InvalidTokenException;
import kr.co.tumble.common.rest.Response;
import kr.co.tumble.common.rest.RestApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * UserDetailService Class
 * Member Api를 호출하여 사용자 정보를 조회
 */
@Service
@Lazy
@Slf4j
@RequiredArgsConstructor
public class UserDetailService {
	private final RestApiUtil restApiUtil;

	@Value("${app.apiUrl.member}")
	private String memberApiUrl;
	
	public UserDetail getUserDetail(String mbrNo, String langCd) {
		log.debug("@@@@@@@@@@@@@@@@@Cacheable method");
		UserDetail param = new UserDetail().setMbrNo(mbrNo).setLangCd(langCd);
		Response<UserDetail> response = restApiUtil.get(this.memberApiUrl + "/api/member/v1/front/userDetail",
				param, new ParameterizedTypeReference<Response<UserDetail>>() {});
		if ("0000".equals(response.getCode())) {
			return response.getPayload();
		} else {
			throw new InvalidTokenException(response.getCode(), response.getMessage());
		}		
	}
	
	public void getUserDetailCacheEvict(String mbrNo, String langCd) {
		log.debug("@@@@@@@@@@@@@@@@@CacheEvict method");
	}
	
}
