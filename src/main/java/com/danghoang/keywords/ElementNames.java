package com.danghoang.keywords;

import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Đổi By thành tên hằng trong Page (LoginPage.LOGIN_BUTTON) để log và report đọc được.
 * WebUI là static nên Page tự đăng ký khi được tạo; tên chỉ được đọc lúc cần.
 */
final class ElementNames {

    private static final ThreadLocal<Registry> REGISTRY = ThreadLocal.withInitial(Registry::new);

    private static final class Registry {
        private final List<Object> notScanned = new ArrayList<>();
        // So theo chính đối tượng By: hai Page cùng chuỗi By.id("name") vẫn là hai tên khác nhau
        private final Map<By, Label> labels = new IdentityHashMap<>();
    }

    private record Label(String page, String field) {
        @Override
        public String toString() {
            return page.isEmpty() ? field : page + "." + field;
        }
    }

    private ElementNames() {}

    static void register(Object page) {
        REGISTRY.get().notScanned.add(page);
    }

    static void clear() {
        REGISTRY.remove();
    }

    static String of(By by) {
        Label label = find(by);
        return label == null ? by.toString().replaceFirst("^By\\.", "") : label.toString();
    }

    /** Tên hằng, "" nếu không phải hằng của Page. */
    static String fieldOf(By by) {
        Label label = find(by);
        return label == null ? "" : label.field();
    }

    private static Label find(By by) {
        Registry registry = REGISTRY.get();
        registry.notScanned.forEach(page -> scan(page, registry.labels));
        registry.notScanned.clear();
        return registry.labels.get(by);
    }

    private static void scan(Object page, Map<By, Label> labels) {
        for (Class<?> type = page.getClass(); type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (!By.class.isAssignableFrom(field.getType()) || !field.trySetAccessible()) {
                    continue;
                }
                try {
                    By by = (By) field.get(page);
                    if (by != null) {
                        // Locator dùng chung nhiều Page (AdminLocators): chỉ hiện tên hằng, không gán nhầm cho một Page
                        labels.merge(by, new Label(page.toString(), field.getName()),
                                (old, now) -> old.page().equals(now.page()) ? old : new Label("", old.field()));
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Không đọc được locator " + field.getName(), e);
                }
            }
        }
    }
}
