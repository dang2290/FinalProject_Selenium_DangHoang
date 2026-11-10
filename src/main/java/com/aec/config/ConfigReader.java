package com.aec.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;

/**
 * Đọc cấu hình theo thứ tự ưu tiên: -Dkey (System property) → biến môi trường → file .env.
 * CI truyền qua biến môi trường / GitHub Secrets; máy local dùng .env.
 */
public final class ConfigReader {

    private static final ConfigReader INSTANCE = new ConfigReader();

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private ConfigReader() {}

    public static ConfigReader getInstance() {
        return INSTANCE;
    }

    private String get(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
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

    public String getUsername() {
        return required("APP_USERNAME");
    }

    public String getPassword() {
        return required("APP_PASSWORD");
    }
}
