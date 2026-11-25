package com.danghoang.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Đọc configs/config.properties, rồi chồng configs/&lt;môi trường&gt;.properties lên (file sau đè key trùng).
 * Chỉ để cấu hình KHÔNG bí mật: file này lên git. Tài khoản, mật khẩu ở .env.
 */
public final class PropertiesHelper {

    private static final String DIR = "configs/";
    private static final String COMMON = "config";

    private PropertiesHelper() {}

    /** env rỗng: chỉ dùng file chung. env sai tên: báo lỗi ngay, không chạy tiếp với cấu hình sai. */
    public static Properties load(String env) {
        Properties properties = new Properties();
        read(properties, COMMON);
        if (env != null && !env.isBlank()) {
            read(properties, env.trim());
        }
        return properties;
    }

    private static void read(Properties target, String name) {
        String path = DIR + name + ".properties";
        try (InputStream in = PropertiesHelper.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy file cấu hình " + path);
            }
            target.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được " + path, e);
        }
    }
}
