package kr.co.tumble.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UserAgent 객체
 */
public class UserAgent extends BaseCommonEntity {

	private static final long serialVersionUID = 2L;

	// 서버발행아이디(md5(단말키+mac주소))
	@Schema(description = "서버발행아이디(md5(단말키+mac주소))")
	private String deviceId;
	// 단말 모델 명
	@Schema(description = "단말 모델 명")
	private String deviceModel;
	// 푸시 아이디
	@Schema(description = "푸시 아이디")
	private String pushId;
	// 단말 고유 키
	@Schema(description = "단말 고유 키")
	private String appKey;
	// MAC 주소
	@Schema(description = "MAC 주소")
	private String macAddr;
	// OS 종류(10:ios, 20:android)
	@Schema(description = "OS 종류(10:ios, 20:android)")
	private String osType;
	// OS 버전
	@Schema(description = "OS 버전")
	private String osVer;
	// APP 버전
	@Schema(description = "APP 버전")
	private String appVer;
	// 모드
	@Schema(description = "모드")
	private String mode;
	// 앱 마켓 구분
	@Schema(description = "앱 마켓 구분")
	private String appMarketCd;
	// APP 판단
	@Schema(description = "APP 판단")
	private boolean isApp;
	// 모바일여부
	@Schema(description = "모바일여부")
	private boolean isMobile;
	// 회원번호
	@Schema(description = "회원번호")
	private String mbrNo;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getPushId() {
		return pushId;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsVer() {
		return osVer;
	}

	public void setOsVer(String osVer) {
		this.osVer = osVer;
	}

	public String getAppVer() {
		return appVer;
	}

	public void setAppVer(String appVer) {
		this.appVer = appVer;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAppMarketCd() {
		return appMarketCd;
	}

	public void setAppMarketCd(String appMarketCd) {
		this.appMarketCd = appMarketCd;
	}

	public boolean getIsApp() {
		return isApp;
	}

	public void setIsApp(boolean isApp) {
		this.isApp = isApp;
	}

	public boolean isMobile() {
		return isMobile;
	}

	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	public String getMbrNo() {
		return mbrNo;
	}

	public void setMbrNo(String mbrNo) {
		this.mbrNo = mbrNo;
	}

}