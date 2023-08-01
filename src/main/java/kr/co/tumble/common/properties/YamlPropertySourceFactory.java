package kr.co.tumble.common.properties;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlProcessor.ResolutionMethod;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Properties;

/**
 * YamlPropertySourceFactory
 */
@Nonnull
public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResolutionMethod(ResolutionMethod.OVERRIDE_AND_IGNORE);
		factory.setResources(encodedResource.getResource());

		String propertiesName = "";
		if (ObjectUtils.isNotEmpty(encodedResource.getResource()) && StringUtils.isNotBlank(encodedResource.getResource().getFilename())) {
			propertiesName = encodedResource.getResource().getFilename();
		}
		Assert.notNull(propertiesName, "propertiesName is null.");

		Properties properties = factory.getObject();
		Assert.notNull(properties, "properties is null.");

		return new PropertiesPropertySource(propertiesName, properties);
    }

}