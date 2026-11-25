package com.danghoang.reporting;

import com.danghoang.utils.LogUtils;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Cầu nối step → log: MỌI Allure step (của test, @Step của Page, hành động của WebUI) được ghi ra log bằng
 * đúng câu chữ trong report. Muốn thêm log cho một thao tác thì tạo step, không viết log lần hai.
 * <p>Allure tự nạp qua META-INF/services.
 */
public class AllureLogListener implements StepLifecycleListener, TestLifecycleListener {

    public static final String KNOWN_ISSUE = "knownIssue";
    private static final String KNOWN_PREFIX = "[LỖI ĐÃ BIẾT] ";
    /** Allure.step(tên, thân) mở step với tên tạm này rồi mới đổi sang tên thật. */
    private static final String UNNAMED = "step";
    private static final ThreadLocal<Deque<OpenStep>> OPEN = ThreadLocal.withInitial(ArrayDeque::new);

    private static final class OpenStep {
        private final StepResult result;
        private boolean logged;

        private OpenStep(StepResult result) {
            this.result = result;
        }
    }

    @Override
    public void beforeStepStart(StepResult step) {
        OPEN.get().push(new OpenStep(step));
        logStartOnceNamed(step);
    }

    @Override
    public void afterStepUpdate(StepResult step) {
        logStartOnceNamed(step);
    }

    private void logStartOnceNamed(StepResult step) {
        Deque<OpenStep> open = OPEN.get();
        OpenStep current = open.peek();
        if (current == null || current.result != step || current.logged || UNNAMED.equals(step.getName())) {
            return;
        }
        current.logged = true;
        LogUtils.step("{}> {}", "  ".repeat(open.size() - 1), step.getName());
    }

    @Override
    public void beforeStepStop(StepResult step) {
        Deque<OpenStep> open = OPEN.get();
        open.poll();
        int depth = open.size();
        if (step.getStatus() == Status.FAILED || step.getStatus() == Status.BROKEN) {
            LogUtils.stepFailed("{}x {} [{}]", "  ".repeat(depth), step.getName(), step.getStatus());
        } else if (depth == 0) {
            LogUtils.step("OK {}", step.getName());
        }
        if (depth == 0) {
            OPEN.remove();
        }
    }

    /** FAIL của test gắn knownIssue được đánh dấu để report tách khỏi lỗi mới (xem allure/categories.json). */
    @Override
    public void beforeTestStop(TestResult result) {
        StatusDetails details = result.getStatusDetails();
        if (result.getStatus() != Status.FAILED || details == null) {
            return;
        }
        boolean known = result.getLabels().stream()
                .anyMatch(label -> "tag".equals(label.getName()) && KNOWN_ISSUE.equals(label.getValue()));
        if (known) {
            details.setKnown(true);
            details.setMessage(KNOWN_PREFIX + details.getMessage());
        }
    }
}
