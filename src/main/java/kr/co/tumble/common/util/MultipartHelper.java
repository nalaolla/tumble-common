package kr.co.tumble.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * MultipartHelper Class
 * HttpServletRequest 객체에서 MultipartFile 객체를 다음 형태로 handle Callback 함수로 제공
 * String fieldName, String fileName, long fileSize, InputStream inputStream, MultipartFile file
 */
public final class MultipartHelper {

    private MultipartHelper() {}

    public interface FilePartHandler {
        void handle(String fieldName, String fileName, long fileSize, InputStream inputStream, MultipartFile file);
    }

    @SuppressWarnings("squid:S2674")
    public static void handle(final HttpServletRequest request, final FilePartHandler filePartHandler) throws IOException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

        while (iterator.hasNext()) {
            final MultipartFile file = multipartHttpServletRequest.getFile(iterator.next());
            if (file != null) {
                try (final InputStream inputStream = file.getInputStream()) {
                    if (filePartHandler != null) {
                        filePartHandler.handle(file.getName(), file.getOriginalFilename(), file.getSize(), inputStream, file);
                    } else {
                        inputStream.skip(inputStream.available());
                    }
                } catch (IOException e) {
                    throw new IOException(e);
                }
            }
        }
    }
}