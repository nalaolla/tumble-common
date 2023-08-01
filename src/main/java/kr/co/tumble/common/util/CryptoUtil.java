package kr.co.tumble.common.util;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Optional;

/**
 * CryptoUtil
 * Bcrypt암호화 및 AES256암호화 기능
 */
@Component("cryptoUtil")
@Scope(value = "prototype")
@Slf4j
public class CryptoUtil {

	private static final String DEFAULT_SECRET_KEY = "X2BEE_Application_DATA_SecretKey";
	private static final int GCM_TAG_LENGTH = 16;
	private static final String MODE = "AES/GCM/NoPadding";
	private static final String ALGORITHM = "AES";

	private static Key keySpec;
	private static String iv;

	@Value("${crypto.secret.key:#{null}}")
	private String secretKey;

	private CryptoUtil() {
		setCrypto(secretKey);
	}

	@PostConstruct
	void init() {
		setCrypto(secretKey);
	}

	private static class InnerCryptoUtil {
		private static final CryptoUtil instance = new CryptoUtil();
	}

	public static CryptoUtil getInstance() {
		return InnerCryptoUtil.instance;
	}

	/**
	 * SecretKey를 설정함.
	 */
	public void setSecretKey(String setSecretKey) {
		secretKey = Optional.ofNullable(setSecretKey).orElse("");
		setCrypto(secretKey);
	}

	private static void setCrypto(String secretKey) {
		String key = Optional.ofNullable(secretKey).orElse(DEFAULT_SECRET_KEY);
		Key secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
		String ivKey = key.substring(0, 16);

		keySpec = secretKeySpec;
		iv = ivKey;
	}

	/**
	 * BCrypt 해싱함수를 사용하여 비밀번호를 Encode
	 * @return
	 */
	public String encodeBcrypt(String planeValue) {
		String encodeValue = "";

		if (StringUtils.isNotBlank(planeValue)) {
			try {
				encodeValue = new BCryptPasswordEncoder().encode(planeValue);
			} catch (Exception e) {
				log.debug("encodeBcrypt : {}", e.getMessage());
				encodeValue = "";
			}
		}

		return encodeValue;
	}

	/**
	 * 평문의 비밀번호화 인코딩된 암호가 일치하는지 확인
	 * @return
	 */
	public Boolean matchesBcrypt(String planeValue, String encodeValue) {
		Boolean matche = false;

		try {
			matche = new BCryptPasswordEncoder().matches(planeValue, encodeValue);
		} catch (Exception e) {
			log.debug("matchesBcrypt : {}", e.getMessage());
			matche = false;
		}

		return matche;
	}

	/**
	 * AES256 Encode
	 */
	public String encodeAes(String planeValue) {
		String encodeValue = "";

		if (StringUtils.isNotBlank(planeValue)) {
			try {
				Cipher cipher = Cipher.getInstance(MODE);
				byte[] ivByte = iv.getBytes(StandardCharsets.UTF_8);
				cipher.init(Cipher.ENCRYPT_MODE, keySpec, getGCMParameterSpec(ivByte));

				byte[] encrypted = cipher.doFinal(planeValue.getBytes(StandardCharsets.UTF_8));
				encodeValue = DatatypeConverter.printBase64Binary(encrypted);
			} catch (Exception e) {
				log.debug("encodeAes : {}", e.getMessage());
				encodeValue = "";
			}
		}

		return encodeValue;
	}

	/**
	 * AES256 Decode
	 */
	public String decodeAes(String encodeValue) {
		String decodeValue = "";

		if (StringUtils.isNotBlank(encodeValue)) {
			try {
				Cipher cipher = Cipher.getInstance(MODE);
				byte[] ivByte = iv.getBytes(StandardCharsets.UTF_8);
				cipher.init(Cipher.DECRYPT_MODE, keySpec, getGCMParameterSpec(ivByte));

				byte[] parseBase64Binary = DatatypeConverter.parseBase64Binary(encodeValue);
				byte[] decrypted = cipher.doFinal(parseBase64Binary);
				decodeValue = new String(decrypted, StandardCharsets.UTF_8);
			} catch (Exception e) {
				log.debug("decodeAes : {}", e.getMessage());
				decodeValue = "";
			}
		}

		return decodeValue;
	}

	/**
	 * SHA256 Encode
	 */
    public String encodeSHA256Salt(String str, String salt) {
		String passACL = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
		md.reset();
		md.update(salt.getBytes());
		byte[] raw = md.digest(str.getBytes());
		
		md.reset();
		raw = md.digest(raw);
		
		byte[] encodedBytes = Base64.encodeBase64(encodeHex(raw).getBytes());
		passACL = new String(encodedBytes);
		
		return passACL;
	}

	/**
	 * Hex Encode
	 */
    private String encodeHex(byte[] b) {
		char[] c = Hex.encodeHex(b);
		return new String(c);
	}

	private GCMParameterSpec getGCMParameterSpec (byte[] ivByte) {
		return new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivByte);
	}

}