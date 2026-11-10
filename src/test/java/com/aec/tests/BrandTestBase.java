package com.aec.tests;

import com.aec.base.BaseTest;
import com.aec.pages.BrandEditPage;
import com.aec.pages.BrandPage;
import com.aec.utils.TestDataGenerator;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiền đề chung của TC Brand (docs/testcases/brand/web/test_cases_brand_web.md):
 * PC-1 đăng nhập + mở All Brands · PC-2 tạo brand test · PC-3 dọn mọi brand do test tạo.
 * Môi trường dùng chung: mọi brand test mang mã truy vết riêng, chỉ brand chứa mã đó bị xoá.
 */
abstract class BrandTestBase extends BaseTest {

    protected static final String INSERTED = "Brand has been inserted successfully";
    protected static final String UPDATED = "Brand has been updated successfully";
    protected static final String DELETED = "Brand has been deleted successfully";

    private static final Logger LOG = LogManager.getLogger(BrandTestBase.class);
    private static final int MAX_CLEANUP_ROUNDS = 10;
    private final ThreadLocal<List<String>> tokens = ThreadLocal.withInitial(ArrayList::new);

    /** Mã truy vết cho dữ liệu của test — brand chứa mã này sẽ bị xoá khi test kết thúc. */
    protected String newToken(String testName) {
        String token = TestDataGenerator.token(testName);
        tokens.get().add(token);
        return token;
    }

    /** PC-1: đăng nhập admin rồi mở All Brands. */
    protected BrandPage openBrandsAsAdmin() {
        loginAsAdmin();
        return Allure.step("Arrange: Mở trang All Brands", () -> new BrandPage(driver()).open());
    }

    /** PC-2: tạo brand chỉ có Name bằng form Add New Brand. */
    protected void createBrand(BrandPage brandPage, String name) {
        Allure.step("Arrange: Tạo brand test " + name, () -> {
            brandPage.open();
            brandPage.createBrand(name);
            Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Tạo brand test phải thành công");
        });
    }

    /** Tìm brand test rồi mở form sửa của nó. */
    protected BrandEditPage openEditOf(BrandPage brandPage, String name) {
        return Allure.step("Arrange: Mở form sửa của " + name, () -> {
            brandPage.open();
            brandPage.search(name);
            Assert.assertEquals(brandPage.rowCount(), 1, "Phải tìm thấy đúng 1 brand test");
            return brandPage.editFirstResult();
        });
    }

    @Override
    protected void cleanUpTestData() {
        List<String> created = new ArrayList<>(tokens.get());
        tokens.remove();
        for (String token : created) {
            try {
                Allure.step("Dọn dữ liệu: xoá brand chứa " + token, () -> deleteBrandsContaining(token));
            } catch (RuntimeException e) {
                LOG.warn("Không dọn được brand chứa {} — cần xoá tay", token, e);
            }
        }
    }

    private void deleteBrandsContaining(String token) {
        dismissAlertIfOpen();
        BrandPage brandPage = new BrandPage(driver());
        for (int round = 0; round < MAX_CLEANUP_ROUNDS; round++) {
            brandPage.open();
            brandPage.search(token);
            List<String> names = brandPage.getResultNames();
            if (names.isEmpty()) {
                return;
            }
            if (!names.get(0).toLowerCase().contains(token.toLowerCase())) {
                throw new IllegalStateException("Dòng đầu không phải brand test: " + names.get(0));
            }
            brandPage.deleteFirstResult();
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
