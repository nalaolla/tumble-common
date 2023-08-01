package kr.co.tumble.common.util;


import kr.co.tumble.common.Validator;
import kr.co.tumble.common.constant.TumbleConstants;
import kr.co.tumble.common.exception.UserDefinedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;

/**
 * FileUtil Class
 */
@Slf4j
public class FileUtil {
	private static final int BUFFER_SIZE = 8192;
    private FileUtil() {
    	throw new UnsupportedOperationException();
    }

    /**
     * 파일 임시 저장
     * @name_ko 파일 임시 저장
     */
    public static File saveTempFile(MultipartFile filePart, String strTempUploadDir)  {
    	Validator.throwIfNull(filePart, "MultipartFile cannot be null");
    	Validator.throwIfNull(strTempUploadDir, "strTempUploadDir cannot be null");

        SecureRandom random = new SecureRandom();
        long randomNumber = random.nextLong(Long.MAX_VALUE);
        byte[] uploadedBytes;
		try{
			uploadedBytes = filePart.getBytes();
		} catch (IOException e) {
			throw new UserDefinedException(e);
		}

        File uploadDir = new File(strTempUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File targetFile = new File(uploadDir, String.format("%s%s", randomNumber, getExtension(filePart)));

        if (targetFile.exists()) {
            try {
                Files.delete(targetFile.toPath());
            } catch (IOException e) {
                log.debug("saveTempFile delete {}", e);
            }
        }

        try {
			FileUtils.writeByteArrayToFile(targetFile, uploadedBytes);
		} catch (IOException e) {
			throw new UserDefinedException(e);
		}

        return targetFile;
    }

    /**
     * 유저 템프 디렉터리 조회
     * @name_ko 유저 템프 디렉터리 조회
     */
    public static File getUserTempDirectory(String tempDir, String loginId) {
        File tempDirectory = new File(tempDir, loginId);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }

        return tempDirectory;
    }

    public static void moveFile(File sourceFile, File targetFile) {
        copyFile(sourceFile, targetFile);

        try {
            Files.delete(sourceFile.toPath());
        } catch (IOException e) {
            log.debug("moveFile delete {}", e);
        }
    }

    /**
     * 디렉토리 경로 확인 후 없으면 생성.
     * @param path
     */
    public static void checkDir(String path) {
    	File dir= new File(path);
        if (!dir.exists()) {
        	dir.mkdirs();
        }
    }

    /**
     * 파일 복사
     * @name_ko 파일 복사
     */
    public static void copyFile(File sourceFile, File targetFile)  {
    	Validator.throwIfNull(sourceFile, "sourceFile cannot be null");
    	Validator.throwIfNull(targetFile, "targetFile cannot be null");

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
        		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));){
        	transfer(inputStream, outputStream);
        } catch (Exception e) {
        	throw new UserDefinedException(e);
        }
    }

	public static void transfer(BufferedInputStream inputStream, BufferedOutputStream outputStream)
			throws IOException {
		int read;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((read = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, read);
		}
		outputStream.flush();
	}

    /**
     * 파일 확장자 반환
     * @name_ko 파일 확장자 반환
     */
    public static String getExtension(MultipartFile filePart) {
        String value = "";

        if (StringUtils.isNotBlank(filePart.getOriginalFilename())) {
            try  {
                value = getExtension(filePart.getOriginalFilename());
            } catch (Exception e) {
                value = "";
            }
        }

        return value;
    }

    /**
     * 파일 확장자 반환
     * @name_ko 파일 확장자 반환
     */
    public static String getExtension(File file) {
        return getExtension(file.getAbsolutePath());
    }

    /**
     * 파일 확장자 반환
     * @name_ko 파일 확장자 반환
     */
    public static String getExtension(String fullName) {
    	int lastIndexOfPeriod = fullName.lastIndexOf(TumbleConstants.PERIOD);

    	return lastIndexOfPeriod == -1
    			? TumbleConstants.EMPTY
    			: TumbleConstants.PERIOD + fullName.substring(fullName.lastIndexOf(TumbleConstants.PERIOD) + 1);
    }

    /**
	 * 파일 확장자 체크
	 * 위의 목록과 맞으면 true, 아니면 false;
	 * @param fileName
	 * @param allowExtension
	 *
	 * @name_ko 파일 확장자 체크
	 */
    public static boolean checkAllowExtension(String fileName, String allowExtension) {
    	String fileExtension = FileUtil.getExtension(fileName).toLowerCase().replace(".", "");
    	return allowExtension.indexOf(fileExtension) > -1;
    }

    /**
	 * 파일 확장자 체크
	 * 가능 확장자 PPTX, DOC, EXCEL, JPG, PDF, ZIP
	 * 위의 목록과 맞으면 true, 아니면 false;
	 * @param file
	 *
	 * @name_ko 파일 확장자 체크
	 */
    public static boolean checkAllowExtension(File file, String allowExtension) {
    	String fileExtension = FileUtil.getExtension(file).toLowerCase().replace(".", "");
    	return allowExtension.indexOf(fileExtension) > -1;
    }

}