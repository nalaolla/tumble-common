package kr.co.tumble.common.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * Swearword 객체
 */
@Getter
@Setter
public class Swearword extends BaseCommonEntity {

    private	String bwSeq;	    //금칙어순번
    private	String bwNm;	    //금칙어명(한국어)
    private	String bwDesc;		//금칙어설명(한국어)
    private String bwNmMl;		//금칙어명(다국어)
    private String bwDescMl;	//금칙어설명(다국어)
    private	String useYn;	    //사용여부

    @Getter
    public static class SwearwordReplaceModel {
        private String word;
        private List<String> swearwordList;

        public SwearwordReplaceModel(final String str, final String replacement, final Collection<String> swearwords) {
            this.word = StringUtils.trim(StringUtils.defaultString(str));
            this.swearwordList = swearwords.stream().filter(sw -> StringUtils.contains(this.getWord(), sw)).map(sw -> {
                this.word = StringUtils.replace(this.getWord(), sw, StringUtils.repeat(replacement, StringUtils.length(sw)));
                return sw;
            }).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class SwearwordRequest {
        private String langCd;
        private	String useYn;
    }

}
