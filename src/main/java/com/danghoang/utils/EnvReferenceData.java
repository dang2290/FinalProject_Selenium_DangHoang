package com.danghoang.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Dữ liệu có sẵn trên môi trường test (người bán, danh mục, ảnh thư viện…) mà test dựa vào —
 * gom ở testdata/env_reference.json để đổi môi trường chỉ sửa một file.
 */
public final class EnvReferenceData {

    private static final String RESOURCE = "testdata/env_reference.json";
    private static final Map<String, String> DATA = load();

    private EnvReferenceData() {}

    public static String get(String key) {
        String value = DATA.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Thiếu khoá '" + key + "' trong " + RESOURCE);
        }
        return value;
    }

    private static Map<String, String> load() {
        try (InputStream in = EnvReferenceData.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy dữ liệu môi trường: " + RESOURCE);
            }
            return new ObjectMapper().readValue(in, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được " + RESOURCE, e);
        }
    }
}
