package kr.co.tumble.common.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * BaseFoEntityV1 객체
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseFoEntityV1 {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(example = "2023-12-31T00:00:00")
    private LocalDateTime sysRegDtm;

    @Schema(example = "x2bee")
    private String sysRegId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(example = "2023-12-31T00:00:00")
    private LocalDateTime sysModDtm;

    @Schema(example = "x2bee")
    private String sysModId;

    @Schema(example = "menuid")
    private String sysRegMenuId;

    @Schema(example = "127.0.0.1")
    private String sysRegIpAddr;

    @Schema(example = "menuid")
    private String sysModMenuId;

    @Schema(example = "127.0.0.1")
    private String sysModIpAddr;

}
