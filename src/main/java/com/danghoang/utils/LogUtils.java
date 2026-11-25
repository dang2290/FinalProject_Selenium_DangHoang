package com.danghoang.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cửa DUY NHẤT để ghi log (Log4j2), gọi static ở mọi tầng. Cấu hình ở log4j2.xml.
 * Dùng {} để chèn giá trị: {@code LogUtils.info("Mở {}", url)}.
 * Thao tác trên web không cần gọi: WebUI tạo step và step tự ra log.
 */
public final class LogUtils {

    private static final Logger LOG = LogManager.getLogger("com.danghoang.Log");
    private static final Logger STEP = LogManager.getLogger("com.danghoang.Step");
    private static final Logger STATUS = LogManager.getLogger("com.danghoang.Status");

    private LogUtils() {}

    public static void info(String message, Object... args) {
        LOG.info(message, args);
    }

    public static void warn(String message, Object... args) {
        LOG.warn(message, args);
    }

    public static void error(String message, Object... args) {
        LOG.error(message, args);
    }

    public static void debug(String message, Object... args) {
        LOG.debug(message, args);
    }

    /** Dòng log của một step (AllureLogListener gọi). */
    public static void step(String message, Object... args) {
        STEP.info(message, args);
    }

    public static void stepFailed(String message, Object... args) {
        STEP.error(message, args);
    }

    /** Trạng thái test (TestListener gọi): hiện cả ở console. */
    public static void status(String message, Object... args) {
        STATUS.info(message, args);
    }

    public static void statusWarn(String message, Object... args) {
        STATUS.warn(message, args);
    }

    public static void statusError(String message, Object... args) {
        STATUS.error(message, args);
    }
}
