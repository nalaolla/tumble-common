package kr.co.tumble.common.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {
	private String userName;
	private String mbrNo;
	private String mbrMgrCd;
	private String mbrGbCd;
	private String loginId;
	private String mbrGradeCd;
	private String stafYn;
	private String sexGbCd;
	private String adlCertiYn;
	private String age;
	private String langCd;
	private String comGbCd;


}
