package kr.co.tumble.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseCommonEntity 객체
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseCommonEntity extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 3654058779853559877L;

	private String siteNo = "1";
	private String langCd = "ko";

    private LocalDateTime sysRegDtm;
    private String sysRegId;
    private LocalDateTime sysModDtm;
    private String sysModId;

    private int rowsPerPage;
    private int pageIdx;
    private int pageCalc;
    private String systemDefaultLanguage;
    private String dbLocaleLanguage;
    private String state;
    private String defaultCntryCd;
    private String sysRegrNm;
    private String sysModrNm;

    private String empty = "EMPTY";
    private String sysRegMenuId = this.empty;
    private String sysRegIpAddr = this.empty;
    private String sysModMenuId = this.empty;
    private String sysModIpAddr = this.empty;

    // 대용량 엑셀다운로드
    private String fieldNames;
    private String texts;
    private String widths;
    private String excelTitle;
    private String excelYn;
    private String excelCurPage;
    private String excelCnt;
    private String excelTcnt;

	//개인정보조회 로그 생성용
	private String qryGbCd;			//조회구분코드
	private String indInfoQryCaus;	//개인정보조회사유
    
}
