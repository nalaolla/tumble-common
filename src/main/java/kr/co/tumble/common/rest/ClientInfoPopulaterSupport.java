package kr.co.tumble.common.rest;


import kr.co.tumble.common.entity.BaseCommonEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * ClientInfoPopulaterSupport Class
 * ClientInfo 지원 모듈
 * 공통 시스템 정보등을 Aop를 통해 모델 정보에 Set
 */
public class ClientInfoPopulaterSupport {

	public void populateClientInfo(Object o) {
		if ( Objects.isNull(o) ) {
			return;
		}
		
		if (o instanceof Collection) {
			populateClientInfo((Collection<?>)o);
		} else if (o instanceof Map) {
			populateClientInfo((Map<?,?>)o);
		} else if (o.getClass().isArray()) {
			populateClientInfo(Arrays.asList((Object[])o));
		} else if (o instanceof BaseCommonEntity entity) {
			ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
			if (Objects.nonNull(clientInfo)) {
				entity.setDbLocaleLanguage(clientInfo.getDbLocaleLanguage());

				if (StringUtils.isNotBlank(clientInfo.getSysMenuId())) {
					entity.setSysRegMenuId(clientInfo.getSysMenuId());
					entity.setSysModMenuId(clientInfo.getSysMenuId());
				}

				if (StringUtils.isNotBlank(clientInfo.getSysIpAddr())) {
					entity.setSysRegIpAddr(clientInfo.getSysIpAddr());
					entity.setSysModIpAddr(clientInfo.getSysIpAddr());
				}

				// FO API SysRegId 공통 처리
				if (StringUtils.isBlank(entity.getSysRegId())
						&& StringUtils.isNotBlank(clientInfo.getMbrNo())) {
					entity.setSysRegId(clientInfo.getMbrNo());
				}

				// FO API SysModId 공통 처리
				if (StringUtils.isBlank(entity.getSysModId())
						&& StringUtils.isNotBlank(clientInfo.getMbrNo())) {
					entity.setSysModId(clientInfo.getMbrNo());
				}

				// BO API SysRegId 공통 처리
				if (StringUtils.isBlank(entity.getSysRegId())
						&& StringUtils.isNotBlank(clientInfo.getLoginId())) {
					entity.setSysRegId(clientInfo.getLoginId());
				}

				// BO API SysModId 공통 처리
				if (StringUtils.isBlank(entity.getSysModId())
						&& StringUtils.isNotBlank(clientInfo.getLoginId())) {
					entity.setSysModId(clientInfo.getLoginId());
				}
			}
		}
	}
	
	private void populateClientInfo(Collection<?> collection) {
		if ( ! CollectionUtils.isEmpty(collection) ) {
			for (Object object : collection) {
				populateClientInfo(object);
			}
		}
	}

	private void populateClientInfo(Map<?,?> map) {
		if ( ! CollectionUtils.isEmpty(map) ) {
			populateClientInfo(map.values());
		}
	}

}
