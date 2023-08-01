package kr.co.tumble.common.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;

@Setter
@Getter
@ToString
public class ClientInfo {

	public static final String CLIENT_INFO_HEADER_NAME = "X-ClientInfo";
	
	private String dbLocaleLanguage;
	private String dbTimeZone;
	private String javaTimeZone;
	private Boolean userAgentIsApp = false;
	private String mbrNo;
	private String loginId;
	private String sysMenuId;
	private String sysIpAddr;
	private String siteNo;
	
	public static ClientInfo defaultValue() {
		ClientInfo info = new ClientInfo();
		info.setDbLocaleLanguage(Locale.getDefault().getLanguage());
		info.setDbTimeZone("UTC");
		info.setJavaTimeZone("UTC");
		return info;
	}

}
