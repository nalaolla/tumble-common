package kr.co.tumble.common.util;

import org.springframework.beans.factory.config.PropertiesFactoryBean;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * PropertiesUtil
 */
public class PropertiesUtils {

	private PropertiesUtils() {}

	public static String getProperty(PropertiesFactoryBean domainConfig, String propertyName) {
		Properties prop;
		String result = "";
		try {
			prop = Optional.of(domainConfig.getObject()).orElse(new Properties());
			result = prop != null ? prop.getProperty(propertyName) : "";
		} catch (IOException e) {
			result = "";
		}
		return result;
	}
}
