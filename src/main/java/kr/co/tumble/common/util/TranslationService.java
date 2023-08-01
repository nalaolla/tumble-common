package kr.co.tumble.common.util;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import kr.co.tumble.common.exception.CommonException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * TranslationService Class
 */
public class TranslationService {

    private Translate translate;

	public TranslationService(Resource serviceAccountKey) throws IOException {
		try (InputStream serviceKeyInputStream = serviceAccountKey.getInputStream()) {
			this.translate = TranslateOptions.newBuilder()
	        		.setCredentials(ServiceAccountCredentials.fromStream(serviceKeyInputStream))
	        		.build().getService();
		}
	}
	
    /**
     * Translate String to target language
     * @param str 번역 대상 문자열
     * @param sourceLanguage 번역 대상 문자열의 language code
     * @param targetLanguage 번연하려는 language code
     * @return 번역된 문자열
     */
    public String translate(String str, String sourceLanguage, String targetLanguage) {

        String translatedText = "";

        if (str==null || str.length()==0) {
            return str;
        }

        if (targetLanguage==null || targetLanguage.length()==0) {
            return str;
        }

        if (sourceLanguage==null || sourceLanguage.length()==0) {
            sourceLanguage = detectLanguage(str);
        }

        if (sourceLanguage.equals(targetLanguage)) { // Bad language pair
            return str;
        }

        try{
            translatedText = translate.translate(
                            str
                            , Translate.TranslateOption.sourceLanguage(sourceLanguage)
                            , Translate.TranslateOption.targetLanguage(targetLanguage)).getTranslatedText();
            return translatedText;

        } catch (Exception e) {
            throw new CommonException(e);
        }

    }

    /**
     * Translate String to target language
     * @param str 번역 대상 문자열
     * @param targetLanguage 번연하려는 language code
     * @return 번역된 문자열
     */
    public String translate(String str, String targetLanguage) {

        String translatedText = "";
        String sourceLanguage = "";

        if (str==null || str.length()==0) {
            return str;
        }

        if (targetLanguage==null || targetLanguage.length()==0) {
            return str;
        }

        try{
            sourceLanguage = translate.detect(str).getLanguage();
            if (sourceLanguage.equals(targetLanguage)) { // Bad language pair
                return str;
            }

            translatedText = translate.translate(
                    str
                    , Translate.TranslateOption.sourceLanguage(sourceLanguage)
                    , Translate.TranslateOption.targetLanguage(targetLanguage)).getTranslatedText();
            return translatedText;

        } catch (Exception e) {
            throw new CommonException(e);
        }

    }

    /**
     * Detect the language of String
     *      - Name: English, Code: en
     *      - Name: Korean, Code: ko
     *      - Name: Chinese (Simplified), Code: zh
     *      - Name: Japanese, Code: ja
     * @param str 
     * @return language code
     */
    public String detectLanguage(String str) {
        try{
            Detection detection = translate.detect(str);
            return detection.getLanguage();
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }	

}