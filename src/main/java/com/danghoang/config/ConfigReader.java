package com.danghoang.config;

import com.danghoang.helpers.PropertiesHelper;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.util.Properties;

/**
 * Nguồn cấu hình DUY NHẤT (vai trò ParameterManager + ConfigData), theo thứ tự ưu tiên:
 * -Dkey → biến môi trường → .env → configs/&lt;TEST_ENV&gt;.properties → configs/config.properties.
 * CI truyền qua biến môi trường / GitHub Secrets; máy local dùng .env.
 */
public final class ConfigReader {

    private static final ConfigReader INSTANCE = new ConfigReader();

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private final Properties properties;
    private final boolean screenshotAllSteps;

    private ConfigReader() {
        properties = PropertiesHelper.load(fromOverrides("TEST_ENV"));
        screenshotAllSteps = Boolean.parseBoolean(get("SCREENSHOT_ALL_STEPS", "false"));
    }

    public static ConfigReader getInstance() {
        return INSTANCE;
    }

    private String get(String key, String defaultValue) {
        String value = fromOverrides(key);
        if (value == null || value.isBlank()) {
            value = properties.getProperty(key);
        }
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    private String fromOverrides(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }
        return value;
    }

    private String required(String key) {
        String value = get(key, null);
        if (value == null) {
            throw new IllegalStateException("Thiếu cấu hình " + key + " — khai trong .env hoặc biến môi trường");
        }
        return value;
    }

    /** Chỉ lấy gốc site (giao thức + tên miền): BASE_URL ghi kèm đường dẫn như /login vẫn dùng được. */
    public String getBaseUrl() {
        URI uri = URI.create(get("BASE_URL", "https://cms.anhtester.com"));
        return uri.getScheme() + "://" + uri.getRawAuthority();
    }

    public String getBrowser() {
        return get("BROWSER", "chrome").toLowerCase();
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("HEADLESS", "false"));
    }

    public int getTimeout() {
        return Integer.parseInt(get("TIMEOUT", "15"));
    }

    public boolean isScreenshotAllSteps() {
        return screenshotAllSteps;
    }

    public String getUsername() {
        return required("APP_USERNAME");
    }

    public String getPassword() {
        return required("APP_PASSWORD");
    }
}
