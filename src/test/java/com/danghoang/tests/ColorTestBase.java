package com.danghoang.tests;

import com.danghoang.pages.ColorEditPage;
import com.danghoang.pages.ColorListPage;
import com.danghoang.utils.TestDataGenerator;
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
abstract class ColorTestBase extends TrackedDataTestBase {

    protected static final String INSERTED = "Color has been inserted successfully";
    protected static final String UPDATED = "Color has been updated successfully";
    protected static final String DELETED = "Color has been deleted successfully";
    protected static final String NAME_REQUIRED = "The name field is required.";
    protected static final String CODE_REQUIRED = "The code field is required.";
    protected static final String CODE_TAKEN = "The code has already been taken.";
    protected static final String CODE_TOO_LONG = "The code may not be greater than 255 characters.";
    /** Màu mới nằm đầu bảng; chạy song song 5 luồng nên chỉ chắc nằm trong vài dòng đầu. */
    protected static final int TOP_ROWS = 5;

    private static final String HEX = "0123456789abcdef";
    private final ThreadLocal<List<String>> colorIds = ThreadLocal.withInitial(ArrayList::new);

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
        return arrange("Mở trang All Colors", () -> new ColorListPage().open());
    }

    /** PC-2: tạo màu bằng form Add New Color. */
    protected void createColor(ColorListPage list, String name, String code) {
        arrange("Tạo màu test " + name + " · " + code, () -> {
            list.open();
            list.createColor(name, code);
            Assert.assertEquals(list.getNotifyMessage(), INSERTED, "Tạo màu test phải thành công");
        });
    }

    protected ColorEditPage openEditOf(ColorListPage list, String name) {
        return arrange("Mở trang sửa của " + name, () -> {
            int row = list.findRow(name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy màu test " + name);
            return list.editRow(row);
        });
    }

    /**
     * Id của màu vừa tạo mà tên không tìm lại được bằng mã truy vết: xét các dòng đầu trang 1 (dòng trùng tên trước),
     * mở trang sửa, so Color Code. Ghi luôn id để dọn.
     */
    protected String idOfNewColor(ColorListPage list, String name, String code) {
        return arrange("Xác định id của màu vừa tạo theo Color Code " + code, () -> {
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
            ColorEditPage edit = new ColorEditPage();
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
        List<String> ids = takeAll(colorIds);
        List<String> created = takeTokens();
        dismissAlertIfOpen();
        for (String id : ids) {
            String token = TestDataGenerator.token("Del");
            created.add(token);
            runCleanup("đổi tên màu id " + id + " thành " + token, () -> renameForCleanup(id, token));
        }
        for (String token : created) {
            runCleanup("xoá màu chứa " + token, () -> new ColorListPage().deleteAllContaining(token));
        }
    }

    private void renameForCleanup(String id, String token) {
        ColorEditPage edit = new ColorEditPage();
        browser().openPath("/admin/colors/edit/" + id + "?lang=en");
        if (edit.isPageNotFound() || !edit.isTitleDisplayed()) {
            return;
        }
        edit.saveWithName(token);
    }
}
