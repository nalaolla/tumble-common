package kr.co.tumble.common.service;


import kr.co.tumble.common.entity.Swearword;
import kr.co.tumble.common.rest.Response;
import kr.co.tumble.common.rest.RestApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * SwearwordRestService
 */
@Service
@Lazy
@Slf4j
@RequiredArgsConstructor
public class SwearwordRestService {

	private final RestApiUtil restApiUtil;

	@Value("${app.apiUrl.common}")
	private String commonApiUrl;

	private static final String BANWORD_API_URL = "/api/common/v1/system/getBadwordList";
	public Collection<String> getSwearwords(Swearword.SwearwordRequest badWordMgmtRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append(commonApiUrl).append(BANWORD_API_URL);
		Response<Collection<String>> response = restApiUtil.get(sb.toString(), badWordMgmtRequest, new ParameterizedTypeReference<Response<Collection<String>>>() {});
		return response.getPayload();
	}

	public void swearwordCacheEvict(Swearword.SwearwordRequest badWordMgmtRequest) {
		log.info("SwearwordRestService.evictSwearwordCache");
	}
}