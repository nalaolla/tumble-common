package kr.co.tumble.common.token;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TokenRequest {
	// 사용자이름
	@NotEmpty
	private String userName;
	// 비밀번호
	private String password;
	// 토큰유효기간
	private Long validMillis;
}
