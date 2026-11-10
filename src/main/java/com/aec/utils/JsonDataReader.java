package com.aec.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/** Đọc mảng JSON trong src/test/resources thành danh sách bản ghi cho @DataProvider. */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {}

    public static List<Map<String, String>> readList(String resourcePath) {
        try (InputStream in = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy dữ liệu test: " + resourcePath);
            }
            return MAPPER.readValue(in, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được " + resourcePath, e);
        }
    }
}
