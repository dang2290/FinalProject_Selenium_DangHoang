package com.danghoang.tests;

import com.danghoang.pages.BrandEditPage;
import com.danghoang.pages.BrandPage;
import org.testng.Assert;

import java.util.List;

/**
 * Tiền đề chung của TC Brand (docs/testcases/brand/web/test_cases_brand_web.md):
 * PC-1 đăng nhập + mở All Brands · PC-2 tạo brand test · PC-3 dọn mọi brand do test tạo.
 * Môi trường dùng chung: mọi brand test mang mã truy vết riêng, chỉ brand chứa mã đó bị xoá.
 */
abstract class BrandTestBase extends TrackedDataTestBase {

    protected static final String INSERTED = "Brand has been inserted successfully";
    protected static final String UPDATED = "Brand has been updated successfully";
    protected static final String DELETED = "Brand has been deleted successfully";

    private static final int MAX_CLEANUP_ROUNDS = 10;

    /** PC-1: đăng nhập admin rồi mở All Brands. */
    protected BrandPage openBrandsAsAdmin() {
        loginAsAdmin();
        return arrange("Mở trang All Brands", () -> new BrandPage().open());
    }

    /** PC-2: tạo brand chỉ có Name bằng form Add New Brand. */
    protected void createBrand(BrandPage brandPage, String name) {
        arrange("Tạo brand test " + name, () -> {
            brandPage.open();
            brandPage.createBrand(name);
            Assert.assertEquals(brandPage.getNotifyMessage(), INSERTED, "Tạo brand test phải thành công");
        });
    }

    /** Tìm brand test rồi mở form sửa của nó. */
    protected BrandEditPage openEditOf(BrandPage brandPage, String name) {
        return arrange("Mở form sửa của " + name, () -> {
            brandPage.searchFor(name);
            Assert.assertEquals(brandPage.rowCount(), 1, "Phải tìm thấy đúng 1 brand test");
            return brandPage.editFirstResult();
        });
    }

    @Override
    protected void cleanUpTestData() {
        for (String token : takeTokens()) {
            runCleanup("xoá brand chứa " + token, () -> deleteBrandsContaining(token));
        }
    }

    private void deleteBrandsContaining(String token) {
        dismissAlertIfOpen();
        BrandPage brandPage = new BrandPage();
        for (int round = 0; round < MAX_CLEANUP_ROUNDS; round++) {
            brandPage.searchFor(token);
            List<String> names = brandPage.names();
            if (names.isEmpty()) {
                return;
            }
            if (!names.get(0).toLowerCase().contains(token.toLowerCase())) {
                throw new IllegalStateException("Dòng đầu không phải brand test: " + names.get(0));
            }
            brandPage.deleteFirstResult();
        }
    }
}
