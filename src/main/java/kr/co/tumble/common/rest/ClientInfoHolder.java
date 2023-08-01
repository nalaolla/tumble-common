package kr.co.tumble.common.rest;

import kr.co.tumble.common.util.JsonUtils;
import kr.co.tumble.common.util.RequestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * ClientInfo Holder 객체
 * 공통 시스템 정보등을 Request 객체 정보에서 가져와 설정
 */
public interface ClientInfoHolder {

	public static ClientInfo getClientInfo() {
		ClientInfo clientInfo = resolveClientInfo();
		return clientInfo;
	}
	
	private static ClientInfo resolveClientInfo() {
		ClientInfo clientInfo = RequestUtils.getAttribute(ClientInfo.CLIENT_INFO_HEADER_NAME);
		if (clientInfo == null) {
			HttpHeaders headers = RequestUtils.requestHeaders();
			if ( Objects.nonNull(headers) ) {
				String value = headers.getFirst(ClientInfo.CLIENT_INFO_HEADER_NAME);
				if ( StringUtils.hasText(value) ) {
					clientInfo =  JsonUtils.object(value, ClientInfo.class);
					RequestUtils.setAttribute(ClientInfo.CLIENT_INFO_HEADER_NAME, clientInfo);
					return clientInfo;
				}
			}
			
			return ClientInfo.defaultValue();
		} else {
			return clientInfo;
		}
	}
}
