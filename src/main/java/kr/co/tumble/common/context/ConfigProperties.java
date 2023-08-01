package kr.co.tumble.common.context;

import jakarta.annotation.PostConstruct;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.*;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Config Properties Util Class
 * Config Properties Data를 공통으로 관리하기 위한 Class
 * properties 설정값이 실시간으로 변경될 수 있기 때문에 해당 설정 파일을 Trigger를 설정하여 1초마다 변화를 감지함
 */
@Component("configProperties")
public class ConfigProperties {

    // 초기 application.properties 또는 application.yml 파일 설정
    private static final String INIT_CONFIG_FILE = "application.yml";
    private static final String PROPERTIES_CONFIG_FILE_NAME = "pplication.properties";
    private static final String YML_CONFIG_FILE_NAME = "application.yml";

    private static ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder = null;

    private static ReloadingFileBasedConfigurationBuilder<YamlConfiguration> yamlBuilder = null;

    private ConfigProperties() {}

    private static class InnerConfigProperties {
        private static final ConfigProperties instance = new ConfigProperties();
    }

    public static ConfigProperties getInstance() {
        return InnerConfigProperties.instance;
    }

    private static void setConfigBuilder(String configFile) {
        List<FileLocationStrategy> subs = Arrays.asList(
                new ProvidedURLLocationStrategy(),
                new FileSystemLocationStrategy(),
                new ClasspathLocationStrategy());
        FileLocationStrategy strategy = new CombinedLocationStrategy(subs);

        PropertiesBuilderParameters propertiesBuilderParameters = new Parameters().properties()
                .setEncoding("UTF-8")
                .setFileName(configFile)
                .setLocationStrategy(strategy)
                .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                .setReloadingRefreshDelay(2000L)
                .setThrowExceptionOnMissing(true);


        if (isYaml(configFile)) {
            yamlBuilder = new ReloadingFileBasedConfigurationBuilder<>(YamlConfiguration.class)
                    .configure(propertiesBuilderParameters);

            yamlBuilder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST, event -> {

            });

            PeriodicReloadingTrigger configReloadingTrigger = new PeriodicReloadingTrigger(
                    yamlBuilder.getReloadingController(), null, 1, TimeUnit.SECONDS);
            configReloadingTrigger.start();
        } else {
            propertiesBuilder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                    .configure(propertiesBuilderParameters);

            propertiesBuilder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST, event -> {

            });

            PeriodicReloadingTrigger configReloadingTrigger = new PeriodicReloadingTrigger(
                    propertiesBuilder.getReloadingController(), null, 1, TimeUnit.SECONDS);
            configReloadingTrigger.start();
        }
    }

    @PostConstruct
    void init() {
        String configFile = INIT_CONFIG_FILE;

        File file = null;
        if (!isYaml(configFile)) {
            try {
                file = new File(getClass().getClassLoader().getResource(configFile).getFile());
            } catch (Exception ex) {
                file = null;
            }
        }

        if (file == null || !file.exists() || PROPERTIES_CONFIG_FILE_NAME.equals(configFile)) {
            configFile = YML_CONFIG_FILE_NAME;
        }

        setConfigBuilder(configFile);
    }

    private static boolean isYaml(String configFile) {
        boolean result = false;
        try {
            String extension = configFile.substring(configFile.lastIndexOf(".") + 1);
            if ("yml".equals(extension)) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public Configuration getCompositeConfiguration() {
        try {
            if (yamlBuilder != null) {
                return yamlBuilder.getConfiguration();
            } else {
                return propertiesBuilder.getConfiguration();
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getKeys() {
        Iterator<String> keys = getCompositeConfiguration().getKeys();
        List<String> keyList = new ArrayList<>();
        while(keys.hasNext()) {
            keyList.add(keys.next());
        }
        return keyList;
    }

    public String getValue(String key) {
        String data = "";
        try {
            data = getCompositeConfiguration().getString(key, "");
        } catch (Exception e) {
            data = "";
        }
        return data;
    }

    public Integer getIntValue(String key) {
        Integer data = null;
        try {
            data = getCompositeConfiguration().getInteger(key, null);
        } catch (Exception e) {
            data = null;
        }
        return data;
    }

    public String[] getArrayValue(String key) {
        String[] data = new String[0];
        try {
            data = getCompositeConfiguration().getStringArray(key);
        } catch (Exception e) {
            data = new String[0];
        }
        return data;
    }

    public List<String> getListValue(String key) {
        List<String> data = new ArrayList<>();
        try {
            String[] value = getArrayValue(key);
            data = new ArrayList<>(Arrays.asList(value));
        } catch (Exception e) {
            data = new ArrayList<>();
        }
        return data;
    }

}
