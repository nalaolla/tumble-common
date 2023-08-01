package kr.co.tumble.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 엑셀다운로드 객체
 */
@Getter
@Setter
public class ExcelEntity {
    
    private String fieldNames;
    private String sheetName;
    private String fileName;
    private List<Map<String, Object>> titles;

    @Builder
    public ExcelEntity(String sheetName, String fileName, List<Map<String, Object>> titles) {
        this.sheetName = sheetName;
        this.fileName = fileName;
        this.titles = titles;
    }

    public ExcelEntity() {}
}
