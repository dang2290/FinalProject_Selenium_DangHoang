package com.danghoang.tests;

import com.danghoang.base.BaseTest;
import com.danghoang.utils.LogUtils;
import com.danghoang.utils.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Phần chung của các test có tạo dữ liệu trên môi trường dùng chung: cấp mã truy vết, giữ theo thread
 * (một instance class dùng cho nhiều thread), dọn mà lỗi một mục không chặn mục sau.
 */
abstract class TrackedDataTestBase extends BaseTest {

    private final ThreadLocal<List<String>> tokens = ThreadLocal.withInitial(ArrayList::new);

    /** Mã truy vết cho dữ liệu của test — bản ghi chính của module chứa mã này sẽ bị xoá khi test kết thúc. */
    protected String newToken(String testName) {
        String token = TestDataGenerator.token(testName);
        tokens.get().add(token);
        return token;
    }

    /** Lấy mọi mã truy vết của test đang dọn và xoá khỏi thread. */
    protected List<String> takeTokens() {
        return takeAll(tokens);
    }

    protected static List<String> takeAll(ThreadLocal<List<String>> tracked) {
        List<String> values = new ArrayList<>(tracked.get());
        tracked.remove();
        return values;
    }

    protected void runCleanup(String title, Runnable action) {
        try {
            cleanUp(title, action::run);
        } catch (RuntimeException e) {
            LogUtils.warn("{} — không dọn được, cần xoá tay", title, e);
        }
    }

    protected void dismissAlertIfOpen() {
        browser().dismissAlertIfOpen();
    }
}
