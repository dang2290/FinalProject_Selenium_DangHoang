package com.aec.utils;

import java.util.concurrent.ThreadLocalRandom;

/** Dữ liệu unique + traceable: [prefix]_[testName]_[timestamp]_[random]. */
public final class TestDataGenerator {

    private TestDataGenerator() {}

    private static String suffix() {
        return System.currentTimeMillis() / 1000 + "_" + Integer.toHexString(ThreadLocalRandom.current().nextInt(0x1000, 0xFFFF)).toUpperCase();
    }

    public static String email(String testName) {
        return String.format("auto_%s_%s@auto.test", testName, suffix()).toLowerCase();
    }

    public static String name(String prefix, String testName) {
        return String.format("%s_%s_%s", prefix, testName, suffix());
    }

    /** Mã truy vết của một test: <testName>_<timestamp>_<random> — nhúng vào mọi dữ liệu test đó tạo để tìm lại và dọn. */
    public static String token(String testName) {
        return testName + "_" + suffix();
    }

    /** Chuỗi bắt đầu bằng head, thêm ký tự fill cho đủ đúng length ký tự. */
    public static String padTo(String head, char fill, int length) {
        return head + String.valueOf(fill).repeat(Math.max(0, length - head.length()));
    }

    public static String password(String testName) {
        return "Wrong_" + testName + "_" + suffix();
    }

    /** Thay {ts} trong dữ liệu mẫu bằng hậu tố unique. */
    public static String resolve(String template) {
        return template.replace("{ts}", suffix());
    }
}
