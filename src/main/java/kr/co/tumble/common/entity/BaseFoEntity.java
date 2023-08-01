package kr.co.tumble.common.entity;

import kr.co.tumble.common.constant.TumbleConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * BaseFoEntity 객체
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseFoEntity {

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
    private String sysRegMenuId = TumbleConstants.EMPTY_STRING;
    private String sysRegIpAddr = TumbleConstants.EMPTY_STRING;
    private String sysModMenuId = TumbleConstants.EMPTY_STRING;
    private String sysModIpAddr = TumbleConstants.EMPTY_STRING;
    
    // 대용량 엑셀다운로드
    private String fieldNames;
    private String texts;
    private String widths;
    private String excelTitle;
    private String excelYn;
    private String excelCurPage;
    private String excelCnt;
    private String excelTcnt;

}
