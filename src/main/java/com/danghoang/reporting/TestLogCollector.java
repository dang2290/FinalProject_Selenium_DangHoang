package com.danghoang.reporting;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.charset.StandardCharsets;

/** Gom log của riêng thread đang chạy test để đính vào report; chạy song song không lẫn log của nhau. */
public final class TestLogCollector extends AbstractAppender {

    private static final String NAME = "TestLogCollector";
    private static final ThreadLocal<StringBuilder> BUFFER = new ThreadLocal<>();

    private TestLogCollector(PatternLayout layout) {
        super(NAME, null, layout, true, Property.EMPTY_ARRAY);
    }

    /** Gắn vào logger com.danghoang (gọi nhiều lần vẫn chỉ gắn một lần). */
    public static synchronized void install() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        var config = context.getConfiguration().getLoggerConfig("com.danghoang");
        if (config.getAppenders().containsKey(NAME)) {
            return;
        }
        TestLogCollector collector = new TestLogCollector(PatternLayout.newBuilder()
                .withPattern("%d{HH:mm:ss} %-5level %msg%n").withCharset(StandardCharsets.UTF_8).build());
        collector.start();
        config.addAppender(collector, Level.INFO, null);
        context.updateLoggers();
    }

    public static void startTest() {
        BUFFER.set(new StringBuilder());
    }

    public static String currentLog() {
        StringBuilder buffer = BUFFER.get();
        return buffer == null ? "" : buffer.toString();
    }

    public static void endTest() {
        BUFFER.remove();
    }

    @Override
    public void append(LogEvent event) {
        StringBuilder buffer = BUFFER.get();
        if (buffer != null) {
            buffer.append(getLayout().toSerializable(event));
        }
    }
}
