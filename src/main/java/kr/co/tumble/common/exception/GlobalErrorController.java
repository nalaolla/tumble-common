package kr.co.tumble.common.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.tumble.common.context.ConfigProperties;
import kr.co.tumble.common.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * GlobalErrorController
 * /error Uri로 맵핑
 */
@Slf4j
public class GlobalErrorController implements ErrorController {

	@RequestMapping("/error")
    public ResponseEntity<Response<Object>> error(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();

        log.error("status_code: {}", request.getAttribute("jakarta.servlet.error.status_code"));
        log.error("exception_type: {}", request.getAttribute("jakarta.servlet.error.exception_type"));
        log.error("message: {}", request.getAttribute("jakarta.servlet.error.message"));
        log.error("request_uri: {}", request.getAttribute("jakarta.servlet.error.request_uri"));
        log.error("exception: {}", request.getAttribute("jakarta.servlet.error.exception"));

        if (status != null) {
            if (status.equals(HttpStatus.BAD_REQUEST.value())) {
                String httpStatusCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
                httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

                return new ResponseEntity<>(
                        Response.builder()
                                .code("0400")
                                .message("BAD_REQUEST")
                                .error(true)
                                .build(),
                        new HttpHeaders(), httpStatus);
            } else if (status.equals(HttpStatus.FORBIDDEN.value())) {
                String httpStatusCode = String.valueOf(HttpStatus.FORBIDDEN.value());
                httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

                return new ResponseEntity<>(
                        Response.builder()
                                .code("0403")
                                .message("FORBIDDEN")
                                .error(true)
                                .build(),
                        new HttpHeaders(), httpStatus);
            } else if (status.equals(HttpStatus.NOT_FOUND.value())) {
                String httpStatusCode = String.valueOf(HttpStatus.NOT_FOUND.value());
                httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

                return new ResponseEntity<>(
                        Response.builder()
                                .code("0404")
                                .message("NOT_FOUND")
                                .error(true)
                                .build(),
                        new HttpHeaders(), httpStatus);
            }
        }

        if (ObjectUtils.isEmpty(httpStatus)) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        return new ResponseEntity<>(
                Response.builder()
                        .code("9000")
                        .message("시스템오류가 발생했습니다.")
                        .error(true)
                        .build(),
                new HttpHeaders(), httpStatus);
    }

    private int getBadRequestStartPrefix() {
        Integer badRequestStartPrefix = null;
        try {
            badRequestStartPrefix = ConfigProperties.getInstance().getIntValue("http-status-bad-request-start-prefix");
        } catch (Exception ex) {
            badRequestStartPrefix = null;
        }
        if (badRequestStartPrefix == null) {
            badRequestStartPrefix = 400;
        }

        return badRequestStartPrefix;
    }

    private int getBadRequestHttpStatusCode(String httpStatusCode) {
        int badRequestStartPrefix = getBadRequestStartPrefix();
        String lastCode = httpStatusCode.substring(httpStatusCode.length() - 2);
        int httpStatus = badRequestStartPrefix + Integer.valueOf(lastCode);
        return httpStatus;
    }

}
