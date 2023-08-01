package kr.co.tumble.common.util;



import kr.co.tumble.common.constant.TumbleConstants;
import kr.co.tumble.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * SignatureUtil Class
 */
@Slf4j
public class SignatureUtil {

	private SignatureUtil() {}

	public static String getTimestamp() {
		final Calendar cal = Calendar.getInstance();
		return (new StringBuilder(String.valueOf(cal.getTimeInMillis()))).toString();
	}

	public static String makeSignature(final Map<String, String> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			throw new CommonException("Parameters can not be empty.");
		} else {
			final String parametersString = calculateString(parameters);
			final String signature;
			try {
				signature = hash(parametersString, "SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new CommonException(e);
			}
			return signature;
		}
	}

	public static String makeSignatureAuth(final Map<String, String> parameters) {
		String wordTstamp = "tstamp" ;
		String wordTotPrice = "TotPrice" ;
		String wordMOID = "MOID";
		String wordMid = "mid";
		
		if (parameters == null || parameters.isEmpty()) {
			throw new CommonException("Parameters can not be empty.");
		}
		
		String stringToSign = "";
		
		String moid = parameters.get(wordMOID);
		String mid = parameters.get(wordMid);
		String tstamp = parameters.get(wordTstamp);
		
		String totPrice = parameters.get(wordTotPrice);
		String tstampKey = (parameters.get(wordTstamp)).substring((parameters.get(wordTstamp)).length()-1);
		
		switch (Integer.parseInt(tstampKey)) {
		case 1:
			stringToSign = new StringBuilder().append(wordMOID+"=").append(moid).append("&"+wordMid+"=").append(mid).append("&"+wordTstamp+"=").append(tstamp).toString();
			break;
		case 2:
			stringToSign = new StringBuilder().append(wordMOID+"=").append(moid).append("&"+wordTstamp+"=").append(tstamp).append("&"+wordMid+"=").append(mid).toString();
			break;
		case 3:
			stringToSign = new StringBuilder().append(wordMid+"=").append(mid).append("&"+wordMOID+"=").append(moid).append("&"+wordTstamp+"=").append(tstamp).toString();
			break;
		case 4:
			stringToSign = new StringBuilder().append(wordMid+"=").append(mid).append("&"+wordTstamp+"=").append(tstamp).append("&"+wordMOID+"=").append(moid).toString();
			break;
		case 5:
			stringToSign = new StringBuilder().append(wordTstamp+"=").append(tstamp).append("&"+wordMid+"=").append(mid).append("&"+wordMOID+"=").append(moid).toString();
			break;
		case 6:
			stringToSign = new StringBuilder().append(wordTstamp+"=").append(tstamp).append("&"+wordMOID+"=").append(moid).append("&"+wordMid+"=").append(mid).toString();
			break;
		case 7:
			stringToSign = new StringBuilder().append(wordTotPrice+"=").append(totPrice).append("&"+wordMid+"=").append(mid).append("&"+wordTstamp+"=").append(tstamp).toString();
			break;
		case 8:
			stringToSign = new StringBuilder().append(wordTotPrice+"=").append(totPrice).append("&"+wordTstamp+"=").append(tstamp).append("&"+wordMid+"=").append(mid).toString();
			break;
		case 9:
			stringToSign = new StringBuilder().append(wordTotPrice+"=").append(totPrice).append("&"+wordMOID+"=").append(moid).append("&"+wordTstamp+"=").append(tstamp).toString();
			break;
		case 0:
			stringToSign = new StringBuilder().append(wordTotPrice+"=").append(totPrice).append("&"+wordTstamp+"=").append(tstamp).append("&"+wordMOID+"=").append(moid).toString();
			break;
		default :
			stringToSign += stringToSign;
		}

		String signature = null;
		try {
			signature = hash(stringToSign, "SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new CommonException(e);
		}

		return signature;
	}

	public static String hash(final String data, final String algorithm) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(data.getBytes(StandardCharsets.UTF_8));
		final byte[] hashbytes = md.digest();
		final StringBuilder sbuilder = new StringBuilder();
		for (int i = 0; i < hashbytes.length; i++) {
			sbuilder.append(String.format("%02x", Integer.valueOf(hashbytes[i] & 0xff)));
		}

		return sbuilder.toString();
	}

	private static String calculateString(final Map<String, String> parameters) {
		final StringBuilder stringToSign = new StringBuilder("");
		Map<String, String> sortedParamMap = null;
		if (parameters instanceof TreeMap) {
			sortedParamMap = new TreeMap<>();
		} else if (parameters instanceof LinkedHashMap) {
			sortedParamMap = new LinkedHashMap<>();
		} else {
			sortedParamMap = new HashMap<>();
		}
		sortedParamMap.putAll(parameters);
		
		for (final Iterator<?> pairs = sortedParamMap.entrySet().iterator(); pairs.hasNext();) {
			final Map.Entry<String, String> pair = (Map.Entry) pairs.next();
			stringToSign.append((pair.getKey()).trim());
			stringToSign.append(TumbleConstants.EQUAL);
			stringToSign.append((pair.getValue()).trim());
			if (pairs.hasNext()) {
				stringToSign.append(TumbleConstants.AND);
			}
		}

		return stringToSign.toString();
	}
	
    public static byte[] sha512NotiHex(String str) {
		byte[] byteData = null;

	 	try {
			MessageDigest sh = MessageDigest.getInstance("SHA-512");
			sh.update(str.getBytes());
			byteData = sh.digest();
		} catch(NoSuchAlgorithmException e) {
			log.debug("sha512NotiHex NoSuchAlgorithmException {}", e);
		}
         
		return byteData;
	}

}