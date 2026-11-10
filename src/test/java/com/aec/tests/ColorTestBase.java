package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.ColorEditPage;
import com.aec.pages.ColorListPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tiền đề chung của TC Colors (docs/testcases/colors/web/test_cases_colors_web.md):
 * PC-1 đăng nhập · PC-2 tạo màu test · PC-3 dọn mọi màu do test tạo.
 * PC-4: không đụng màu có sẵn, không bấm công tắc Color filter activation.
 * Name giữ ≤ 30 ký tự, không dấu cách (máy chủ cắt / xoá) — mọi tên chứa mã truy vết để tìm lại và dọn.
 * Tên không chứa được mã (1 ký tự, bị lưu rỗng, payload dài) thì ghi id màu — khi dọn đổi tên về mã truy vết rồi xoá.
 */
abstract class ColorTestBase extends BaseTest {

    protected static final String INSERTED = "Color has been inserted successfully";
    protected static final String UPDATED = "Color has been updated successfully";
    protected static final String DELETED = "Color has been deleted successfully";
    protected static final String NAME_REQUIRED = "The name field is required.";
    protected static final String CODE_REQUIRED = "The code field is required.";
    protected static final String CODE_TAKEN = "The code has already been taken.";
    protected static final String CODE_TOO_LONG = "The code may not be greater than 255 characters.";
    /** Màu mới nằm đầu bảng; chạy song song 5 luồng nên chỉ chắc nằm trong vài dòng đầu. */
    protected static final int TOP_ROWS = 5;

    private static final Logger LOG = LogManager.getLogger(ColorTestBase.class);
    private static final String HEX = "0123456789abcdef";
    private final ThreadLocal<List<String>> tokens = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<String>> colorIds = ThreadLocal.withInitial(ArrayList::new);

    /** Mã truy vết cho dữ liệu của test — màu có tên chứa mã này sẽ bị xoá khi test kết thúc. */
    protected String newToken(String testName) {
        String token = TestDataGenerator.token(testName);
        tokens.get().add(token);
        return token;
    }

    /** Màu có tên không chứa mã truy vết — dọn theo id. */
    protected void trackColorId(String id) {
        colorIds.get().add(id);
    }

    /** Mã màu hex 6 ký tự ngẫu nhiên, mở đầu bằng chữ (a–f) để thử được khác hoa thường. Mã phải duy nhất toàn hệ thống. */
    protected static String newCode() {
        return "#" + HEX.charAt(10 + ThreadLocalRandom.current().nextInt(6)) + randomHex(5);
    }

    protected static String randomHex(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(HEX.charAt(ThreadLocalRandom.current().nextInt(HEX.length())));
        }
        return sb.toString();
    }

    /** PC-1: đăng nhập admin rồi mở All Colors. */
    protected ColorListPage openColorsAsAdmin() {
        loginAsAdmin();
        return Allure.step("Arrange: Mở trang All Colors", () -> new ColorListPage(driver()).open());
    }

    /** PC-2: tạo màu bằng form Add New Color. */
    protected void createColor(ColorListPage list, String name, String code) {
        Allure.step("Arrange: Tạo màu test " + name + " · " + code, () -> {
            list.open();
            list.createColor(name, code);
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Tạo màu test phải thành công");
        });
    }

    /** Tìm theo từ khoá, trả về vị trí dòng mang đúng tên (-1 nếu không có). */
    protected int findRow(ColorListPage list, String keyword, String name) {
        list.open();
        list.search(keyword);
        return list.rowIndexOf(name);
    }

    protected ColorEditPage openEditOf(ColorListPage list, String name) {
        return Allure.step("Arrange: Mở trang sửa của " + name, () -> {
            int row = findRow(list, name, name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy màu test " + name);
            return list.editRow(row);
        });
    }

    /**
     * Id của màu vừa tạo mà tên không tìm lại được bằng mã truy vết: xét các dòng đầu trang 1 (dòng trùng tên trước),
     * mở trang sửa, so Color Code. Ghi luôn id để dọn.
     */
    protected String idOfNewColor(ColorListPage list, String name, String code) {
        return Allure.step("Arrange: Xác định id của màu vừa tạo theo Color Code " + code, () -> {
            list.open();
            List<String> names = list.names();
            List<String> candidates = new ArrayList<>();
            int limit = Math.min(names.size(), TOP_ROWS * 2);
            for (int i = 0; i < limit; i++) {
                if (names.get(i).equals(name)) {
                    candidates.add(0, list.idOfRow(i));
                } else {
                    candidates.add(list.idOfRow(i));
                }
            }
            ColorEditPage edit = new ColorEditPage(driver());
            for (String id : candidates) {
                if (edit.openById(id).codeValue().equalsIgnoreCase(code.trim())) {
                    trackColorId(id);
                    return id;
                }
            }
            throw new AssertionError("Không tìm thấy màu có Color Code " + code + " ở " + limit + " dòng đầu");
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> ids = new ArrayList<>(colorIds.get());
        List<String> created = new ArrayList<>(tokens.get());
        colorIds.remove();
        tokens.remove();
        dismissAlertIfOpen();
        for (String id : ids) {
            String token = TestDataGenerator.token("Del");
            created.add(token);
            runCleanup("Dọn dữ liệu: đổi tên màu id " + id + " thành " + token, () -> renameForCleanup(id, token));
        }
        for (String token : created) {
            runCleanup("Dọn dữ liệu: xoá màu chứa " + token, () -> new ColorListPage(driver()).deleteAllContaining(token));
        }
    }

    private void renameForCleanup(String id, String token) {
        ColorEditPage edit = new ColorEditPage(driver());
        driver().get(config.getBaseUrl() + "/admin/colors/edit/" + id + "?lang=en");
        if (edit.isPageNotFound() || !edit.isTitleDisplayed()) {
            return;
        }
        edit.enterName(token);
        edit.saveAndWait();
    }

    private void runCleanup(String title, Runnable action) {
        try {
            Allure.step(title, action::run);
        } catch (RuntimeException e) {
            LOG.warn("{} — không dọn được, cần xoá tay", title, e);
        }
    }

    private void dismissAlertIfOpen() {
        try {
            driver().switchTo().alert().dismiss();
        } catch (NoAlertPresentException e) {
            LOG.debug("Không có hộp thoại cần đóng");
        }
    }
}
