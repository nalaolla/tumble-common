package kr.co.tumble.common.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberTokenDto {
	/** 액세스 토큰 */
	private String accessToken;
	/** 리프레시 토큰 */
	private String refreshToken;
	/** 회원번호 */
	private String mbrNo;
}
