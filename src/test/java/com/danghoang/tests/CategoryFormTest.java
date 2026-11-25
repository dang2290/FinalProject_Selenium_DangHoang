package com.danghoang.tests;

import com.danghoang.base.StepNames;
import com.danghoang.dataproviders.DataProviderFactory;
import com.danghoang.utils.EnvReferenceData;
import com.danghoang.pages.CategoryFormPage;
import com.danghoang.pages.CategoryFormPage.ImageField;
import com.danghoang.pages.CategoryListPage;
import com.danghoang.pages.MediaLibraryModal;
import com.danghoang.pages.ProductCreatePage;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.danghoang.utils.TestDataGenerator.padTo;

/**
 * Form Category Information: giao diện, validation từng trường, Slug, đa ngôn ngữ, Parent Category, liên kết Product.
 * Máy chủ cắt Name còn 50 ký tự (AMB-CAT-01) — tên test giữ ≤ 50 ký tự trừ TC kiểm độ dài.
 * TC ở docs/testcases/category/web/test_cases_category_web.md.
 */
@Feature("Manage Category")
@Story("Form danh mục")
public class CategoryFormTest extends CategoryTestBase {

    private static final String MEDIA_KEYWORD = EnvReferenceData.get("mediaKeyword");

    private static final String SLUG_SUFFIX = "-[A-Za-z0-9]{5}$";

    /** Slug tự sinh: Name bỏ mọi ký tự không phải chữ, số (kể cả _) + "-" + 5 ký tự chữ số. */
    private static String generatedSlugPattern(String name) {
        return "^" + name.replaceAll("[^A-Za-z0-9]", "") + SLUG_SUFFIX;
    }

    @Test(groups = {"smoke", "category", "auth", "ui"}, description = "Form tạo danh mục hiển thị đủ trường theo đúng thứ tự và giá trị mặc định")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra bấm Add New category mở /admin/categories/create với thẻ Category Information, đủ nhãn theo thứ tự, mọi ô ở giá trị mặc định: No Parent, Physical, Choose File, Nothing selected.")
    public void testCreateFormElements() {
        tc("AEC_CAT_TC_002", "smoke", "category", "auth", "ui");
        CategoryListPage list = openCategoriesAsAdmin();

        CategoryFormPage form = act("Bấm Add New category", list::clickAddNew);

        verify("Đường dẫn form tạo", () ->
                Assert.assertTrue(form.isOnCreatePage(), "Thanh địa chỉ: " + form.currentUrl()));

        verify("Bảng kiểm form tạo", () -> {
            Assert.assertTrue(form.isTitleDisplayed(), "(REQ-17) Thẻ Category Information");
            Assert.assertEquals(form.labels(), List.of("Name", "Parent Category", "Ordering Number", "Type", "Banner (200x200)",
                    "Icon (32x32)", "Meta Title", "Meta description", "Filtering Attributes"), "(REQ-17) Nhãn từ trên xuống");
            Assert.assertTrue(form.isSaveDisplayed(), "(REQ-17) Nút Save");
            Assert.assertEquals(form.nameValue(), "", "(REQ-17) Ô Name rỗng");
            Assert.assertEquals(form.parentShown(), NO_PARENT, "(REQ-19) Parent Category mặc định");
            Assert.assertEquals(form.orderLevelValue(), "", "(REQ-20) Ordering Number rỗng");
            Assert.assertEquals(form.orderLevelPlaceholder(), "Order Level", "(REQ-20) Gợi ý Ordering Number");
            Assert.assertTrue(form.isOrderLevelHintShown(), "(REQ-20) Chú thích Higher number has high priority");
            Assert.assertEquals(form.typeShown(), "Physical", "(REQ-21) Type mặc định");
            for (ImageField field : ImageField.values()) {
                Assert.assertEquals(form.browseText(field), "Browse", "(REQ-22) Nút Browse ô " + field);
                Assert.assertEquals(form.fileAmountText(field), "Choose File", "(REQ-22) Chữ ô " + field);
            }
            Assert.assertEquals(form.metaTitleValue(), "", "(REQ-23) Meta Title rỗng");
            Assert.assertEquals(form.metaDescriptionValue(), "", "(REQ-23) Meta description rỗng");
            Assert.assertEquals(form.attributesShown(), "Nothing selected", "(REQ-24) Filtering Attributes");
        });
    }

    @Test(groups = {"smoke", "category", "auth", "ui"}, description = "Form sửa danh mục hiển thị đủ trường, tab English được chọn mặc định và có ô Slug")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra form sửa có 2 tab English (đang chọn) / Vietnamese, đủ ô như form tạo cộng ô Slug tự sinh, Name đúng tên, chỉ nhãn Name có biểu tượng ngôn ngữ với chú thích Translatable.")
    public void testEditFormElements() {
        tc("AEC_CAT_TC_003", "smoke", "category", "auth", "ui");
        String name = "AutoCat_" + newToken("TC003");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        CategoryFormPage form = act("Tìm danh mục test, bấm nút bút chì", () -> openEditOf(list, name));

        verify("Bảng kiểm form sửa", () -> {
            Assert.assertTrue(form.isTitleDisplayed(), "(REQ-29) Tiêu đề Category Information");
            Assert.assertEquals(form.languageTabs(), List.of("English", "Vietnamese"), "(REQ-29) Hai tab");
            Assert.assertEquals(form.activeTab(), "English", "(REQ-29) Tab đang chọn");
            Assert.assertEquals(form.labels(), List.of("Name", "Parent Category", "Ordering Number", "Type", "Banner (200x200)",
                    "Icon (32x32)", "Meta Title", "Meta description", "Slug", "Filtering Attributes"), "(REQ-29) Nhãn, có thêm Slug");
            Assert.assertEquals(form.nameValue(), name, "(REQ-29) Ô Name");
            Assert.assertEquals(form.parentShown(), NO_PARENT, "(REQ-29) Parent Category");
            Assert.assertEquals(form.typeShown(), "Physical", "(REQ-29) Type");
            Assert.assertTrue(form.slugValue().matches(generatedSlugPattern(name)), "(REQ-27) Slug tự sinh: " + form.slugValue());
            Assert.assertEquals(form.translatableLabels(), List.of("Name"), "(REQ-30) Chỉ Name có biểu tượng ngôn ngữ");
        });

        verify(StepNames.TRANSLATABLE_HINT, () ->
                Assert.assertEquals(form.translatableTooltip(), "Translatable", "Chú thích biểu tượng ngôn ngữ"));
    }

    @Test(groups = {"regression", "category", "auth", "validation"}, description = "Không lưu được danh mục khi Name trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể a: form tạo để trống Name (Ordering Number 5) rồi Save thì trình duyệt chặn, trang không tải lại, con trỏ ở ô Name.")
    public void testCreateWithEmptyNameBlocked() {
        tc("AEC_CAT_TC_021", "regression", "category", "auth", "validation");
        Allure.parameter("Biến thể", "a — form tạo");
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage();

        act("Mở form tạo, để trống Name, Ordering Number 5 rồi bấm Save", () -> {
            form.openCreate();
            form.enterOrderLevel("5");
            form.clickSave();
        });

        verify(StepNames.NAME_BLOCKED, () -> {
            Assert.assertFalse(form.nameValidationMessage().isEmpty(), "Phải có bong bóng cảnh báo ở ô Name");
            Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
            Assert.assertTrue(form.isNameFocused(), "Con trỏ phải nằm trong ô Name");
        });
    }

    @Test(groups = {"regression", "category", "auth", "validation"}, description = "Không lưu được chỉnh sửa khi Name trống")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể b: form sửa xoá hết Name rồi Save thì trình duyệt chặn, trang không tải lại, danh mục giữ Name cũ.")
    public void testEditWithEmptyNameBlocked() {
        tc("AEC_CAT_TC_021", "regression", "category", "auth", "validation");
        Allure.parameter("Biến thể", "b — form sửa");
        String name = "AutoCat_" + newToken("TC021b");
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);
        CategoryFormPage form = openEditOf(list, name);

        act(StepNames.CLEAR_NAME_AND_SAVE, () -> {
            form.clearName();
            form.clickSave();
        });

        verify(StepNames.NAME_BLOCKED, () -> {
            Assert.assertFalse(form.nameValidationMessage().isEmpty(), "Phải có bong bóng cảnh báo ở ô Name");
            Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
        });

        verify("Danh mục giữ Name cũ", () ->
                Assert.assertTrue(list.findRow(name) >= 0, "Danh mục test phải giữ tên cũ"));
    }

    @Test(dataProvider = "nameLengthsWithin50", dataProviderClass = DataProviderFactory.class, groups = {"regression", "category", "auth", "validation", "boundary"},
            description = "Name có độ dài hợp lệ được lưu nguyên vẹn")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name dài 49 và 50 ký tự lưu thành công, form sửa giữ đúng từng ký tự. Biến thể a (1 ký tự `Q`) không automate: tên 1 ký tự không truy vết được để dọn trên môi trường dùng chung.")
    public void testNameAtValidLength(String variant, int length) {
        tc("AEC_CAT_TC_023", "regression", "category", "auth", "validation", "boundary");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC023" + variant);
        String name = padTo("AutoCat" + length + "_" + token + "_", 'x', length);
        CategoryListPage list = openCategoriesAsAdmin();

        act("Tạo danh mục với Name " + length + " ký tự", () -> createCategory(name));

        CategoryFormPage form = openEditOf(list, name);
        verify("Ô Name đúng từng ký tự", () -> {
            Assert.assertEquals(form.nameValue(), name, "Name sau khi lưu");
            Assert.assertEquals(form.nameValue().length(), length, "Độ dài Name");
        });
    }

    @Test(dataProvider = "nameLengthsOver50", dataProviderClass = DataProviderFactory.class, groups = {"regression", "category", "auth", "validation", "boundary", "knownIssue"},
            description = "Name dài hơn 50 ký tự phải bị chặn kèm thông báo, không được cắt im lặng")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra Name dài 51 và 255 ký tự thì báo lỗi, vẫn ở form tạo, không có danh mục mới. Đang lệch: hệ thống báo thành công và lưu 50 ký tự đầu (AMB-CAT-01).")
    public void testNameTooLongRejected(String variant, int length) {
        tc("AEC_CAT_TC_024", "regression", "category", "auth", "validation", "boundary", "knownIssue");
        Allure.parameter("Biến thể", variant + " — " + length + " ký tự");
        String token = newToken("TC024" + variant);
        String name = padTo("AutoCat" + length + "_" + token + "_", 'x', length);
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage();
        CategoryListPage list = new CategoryListPage();

        act("Nhập Name " + length + " ký tự rồi Save", () -> {
            form.openCreate();
            form.saveWithName(name);
        });

        verify("Vẫn ở form tạo (không chuyển về danh sách như khi lưu thành công)", () ->
                Assert.assertTrue(form.isOnCreatePage(), "Phải vẫn ở form tạo, thực tế chuyển tới: " + form.currentUrl()));

        verify("Không có danh mục mới", () -> {
            list.searchFor(token);
            Assert.assertEquals(list.rowCount(), 0, "Danh mục quá dài không được lưu (kể cả bị cắt)");
        });
    }

    @DataProvider(name = "specialNames", parallel = true)
    public Object[][] specialNames() {
        return new Object[][]{
                {"a", "AutoCat & Đồ dùng (", ")", false},
                {"b", "AutoCat/Sub-", "", false},
                {"c", "1234567890_", "", false},
                {"d", "   AutoCat_", "   ", true}
        };
    }

    @Test(dataProvider = "specialNames", groups = {"regression", "category", "auth", "validation"},
            description = "Name chứa ký tự đặc biệt, số, tiếng Việt và khoảng trắng đầu cuối")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name có &, (), /, tiếng Việt, toàn số, khoảng trắng đầu cuối đều lưu thành công; danh sách hiện đúng (khoảng trắng đầu cuối bị cắt); Slug không chứa dấu cách, &, /, (, ).")
    public void testSpecialCharacterName(String variant, String head, String tail, boolean trimmed) {
        tc("AEC_CAT_TC_025", "regression", "category", "auth", "validation");
        String token = newToken("TC025" + variant);
        String typed = head + token + tail;
        String expected = trimmed ? typed.trim() : typed;
        Allure.parameter("Biến thể", variant + " — [" + typed + "]");
        CategoryListPage list = openCategoriesAsAdmin();

        act("Tạo danh mục với Name " + typed, () -> createCategory(typed));

        verify("Danh sách hiện đúng Name", () -> {
            list.search(token);
            Assert.assertEquals(list.names(), List.of(expected), "Tên ở danh sách");
        });

        CategoryFormPage form = act(StepNames.OPEN_EDIT_FORM, () -> list.editRow(0));
        verify("Slug không chứa dấu cách, &, /, (, )", () ->
                Assert.assertFalse(form.slugValue().matches(".*[ &/()].*"), "Slug: " + form.slugValue()));
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Tạo được hai danh mục trùng tên, mỗi danh mục có Slug riêng")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra tạo 2 danh mục cùng Name đều báo thành công, tìm ra đúng 2 dòng, Slug của hai danh mục khác nhau.")
    public void testDuplicateNameGetsDistinctSlugs() {
        tc("AEC_CAT_TC_026", "regression", "category", "auth");
        String name = "AutoCatDup_" + newToken("TC026");
        CategoryListPage list = openCategoriesAsAdmin();

        act("Tạo 2 danh mục cùng Name", () -> {
            createCategory(name);
            createCategory(name);
        });

        verify("Đúng 2 dòng cùng Name", () -> {
            list.searchFor(name);
            Assert.assertEquals(list.names(), List.of(name, name), "Hai dòng cùng tên");
        });

        verify("Hai Slug khác nhau", () -> {
            String firstSlug = list.editRow(0).slugValue();
            list.searchFor(name);
            String secondSlug = list.editRow(1).slugValue();
            Assert.assertNotEquals(firstSlug, secondSlug, "Slug của hai danh mục trùng tên");
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Ô Parent Category tìm được danh mục và hiện cây có thụt cấp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ô Parent Category ở form tạo: (a) gõ tên cha chỉ còn mục khớp, chọn được; (b) danh mục con hiện với tiền tố --; (c) chọn cha rồi đổi lại No Parent, lưu thì Level 0, Parent —.")
    public void testParentCategoryPicker() {
        tc("AEC_CAT_TC_027", "regression", "category", "auth");
        String token = newToken("TC027");
        String parent = "AutoCatP_" + token;
        String child = "AutoCatC_" + token;
        String noParent = "AutoCatNP_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(parent);
        createChildCategory(child, parent);
        CategoryFormPage form = new CategoryFormPage();

        act("Mở form tạo, gõ tên danh mục cha vào ô tìm của Parent Category", form::openCreate);
        verify("(a) Chỉ còn mục khớp tên cha; chọn được", () -> {
            Assert.assertEquals(form.searchParent(parent), List.of(parent), "Mục khớp");
            form.chooseParent(parent);
            Assert.assertEquals(form.parentShown(), parent, "Ô Parent Category sau khi chọn");
        });

        verify("(b) Danh mục con hiện với tiền tố --", () -> {
            Assert.assertEquals(form.searchParent(child), List.of("-- " + child), "Mục con");
            form.closeParentList();
        });

        act("(c) Đổi lại No Parent, nhập Name, Save", () -> {
            form.chooseParent(NO_PARENT);
            form.saveWithName(noParent);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Lưu phải thành công");
        });

        verify("(c) Level 0, Parent —", () -> {
            int row = list.findRow(noParent);
            Assert.assertEquals(list.parentOf(row), EMPTY_CELL, "Parent Category");
            Assert.assertEquals(list.levelOf(row), "0", "Level");
        });
    }

    @Test(dataProvider = "orderLevels", dataProviderClass = DataProviderFactory.class, groups = {"regression", "category", "auth", "validation", "boundary"},
            description = "Ordering Number xử lý giá trị trống, âm, biên trên, số lẻ và chữ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Ordering Number: trống → 0; gõ chữ không nhận → 0; -5 giữ nguyên; 2147483647 giữ; 2147483648 về 2147483647; 1.5 bị trình duyệt chặn, không tạo danh mục.")
    public void testOrderingNumberValues(String variant, String typed, String expectedLevel) {
        tc("AEC_CAT_TC_028", "regression", "category", "auth", "validation", "boundary");
        Allure.parameter("Biến thể", variant + " — [" + typed + "]");
        String token = newToken("TC028" + variant);
        String name = "AutoCatOL_" + token;
        loginAsAdmin();
        CategoryFormPage form = new CategoryFormPage();
        CategoryListPage list = new CategoryListPage();

        act("Nhập Name, Ordering Number " + typed, () -> {
            form.openCreate();
            form.enterName(name);
            if (!typed.isEmpty()) {
                form.enterOrderLevel(typed);
            }
        });

        if (expectedLevel == null) {
            act(StepNames.CLICK_SAVE, () -> {
                form.clickSave();
            });
            verify("Trình duyệt chặn ở Ordering Number, không có danh mục mới", () -> {
                Assert.assertFalse(form.orderLevelValidationMessage().isEmpty(), "Phải có bong bóng cảnh báo ở Ordering Number");
                Assert.assertTrue(form.isSamePageInstance(), "Không được gửi form");
                Assert.assertEquals(list.findRow(name), -1, "Không được có danh mục mới");
            });
            return;
        }

        verify("Ô Ordering Number trước khi lưu", () -> {
            if ("abc".equals(typed)) {
                Assert.assertEquals(form.orderLevelValue(), "", "Ô số không được nhận chữ");
            }
        });
        act(StepNames.CLICK_SAVE, form::saveAndWait);

        verify("Order Level ở danh sách là " + expectedLevel, () -> {
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Lưu phải thành công");
            int row = list.findRow(name);
            Assert.assertTrue(row >= 0, "Phải tìm thấy danh mục");
            Assert.assertEquals(list.orderLevelOf(row), expectedLevel, "Order Level");
        });
    }

    @DataProvider(name = "categoryTypes", parallel = true)
    public Object[][] categoryTypes() {
        return new Object[][]{{"a", "Physical", "AutoCatPhy_"}, {"b", "Digital", "AutoCatDig_"}};
    }

    @Test(dataProvider = "categoryTypes", groups = {"regression", "category", "auth"},
            description = "Chọn Type Physical hoặc Digital và giữ đúng sau khi lưu")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ô Type có đúng 2 lựa chọn Physical, Digital; chọn rồi lưu thì form sửa giữ đúng Type.")
    public void testTypeKeptAfterSave(String variant, String type, String prefix) {
        tc("AEC_CAT_TC_029", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + " — " + type);
        String name = prefix + newToken("TC029" + variant);
        CategoryListPage list = openCategoriesAsAdmin();
        CategoryFormPage form = new CategoryFormPage();

        verify("Ô Type có đúng Physical, Digital", () ->
                Assert.assertEquals(form.openCreate().typeOptions(), List.of("Physical", "Digital"), "Lựa chọn Type"));

        act("Chọn Type " + type + ", nhập Name, Save", () -> {
            form.chooseType(type);
            form.saveWithName(name);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Lưu phải thành công");
        });

        verify("Form sửa giữ Type " + type, () ->
                Assert.assertEquals(openEditOf(list, name).typeShown(), type, "Type sau khi lưu"));
    }

    @DataProvider(name = "imageFields", parallel = true)
    public Object[][] imageFields() {
        return new Object[][]{
                {"a", ImageField.BANNER, false, "AutoCatBn_"},
                {"b", ImageField.ICON, false, "AutoCatIc_"},
                {"c", ImageField.BANNER, true, "AutoCatRm_"}
        };
    }

    @Test(dataProvider = "imageFields", groups = {"regression", "category", "auth"},
            description = "Chọn và bỏ chọn ảnh Banner, Icon từ thư viện tệp")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra chọn ảnh đầu tiên (lọc brandimg) cho Banner hoặc Icon thì ô báo 1 File selected, có xem trước và form sửa giữ ảnh; bấm × trên ảnh xem trước trước khi lưu thì ô trở về Choose File.")
    public void testBannerAndIconSelection(String variant, ImageField field, boolean removeBeforeSave, String prefix) {
        tc("AEC_CAT_TC_030", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + " — " + field + (removeBeforeSave ? " rồi bỏ chọn" : ""));
        String name = prefix + newToken("TC030" + variant);
        CategoryListPage list = openCategoriesAsAdmin();
        CategoryFormPage form = new CategoryFormPage();

        MediaLibraryModal library = act("Mở form tạo, nhập Name, bấm Browse ở ô " + field, () -> {
            form.openCreate();
            form.enterName(name);
            MediaLibraryModal modal = form.openLibrary(field);
            modal.search(MEDIA_KEYWORD);
            return modal;
        });

        act("Bấm ảnh đầu tiên", () -> library.selectFile(0));
        verify("Ảnh được đánh dấu chọn", () ->
                Assert.assertEquals(library.selectedIndexes(), List.of(0), "Ảnh được đánh dấu"));

        act("Bấm Add Files", library::addFiles);
        verify("Ô báo 1 File selected, có ảnh xem trước", () -> {
            Assert.assertEquals(form.fileAmountText(field), "1 File selected", "Chữ ô " + field);
            Assert.assertEquals(form.previewCount(field), 1, "Ảnh xem trước");
        });

        act("" + (removeBeforeSave ? "Bấm × trên ảnh xem trước rồi Save" : "Bấm Save"), () -> {
            if (removeBeforeSave) {
                form.removePreview(field);
            }
            form.saveAndWait();
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Lưu phải thành công");
        });

        CategoryFormPage edit = openEditOf(list, name);
        verify("Form sửa — ô " + field, () -> {
            if (removeBeforeSave) {
                Assert.assertEquals(edit.fileAmountText(field), "Choose File", "Ô phải trở về Choose File");
                Assert.assertEquals(edit.previewCount(field), 0, "Không được còn ảnh xem trước");
            } else {
                Assert.assertEquals(edit.previewCount(field), 1, "Ô phải còn đúng ảnh đã chọn");
            }
        });
    }

    @DataProvider(name = "metaValues", parallel = true)
    public Object[][] metaValues() {
        return new Object[][]{
                {"a", "Meta AutoCat TC031", "Mo ta danh muc TC031"},
                {"b", "", ""},
                {"c", "", "Dong 1\nDong 2\nDong 3"}
        };
    }

    @Test(dataProvider = "metaValues", groups = {"regression", "category", "auth"},
            description = "Meta Title và Meta description không bắt buộc và lưu đúng nội dung")
    @Severity(SeverityLevel.MINOR)
    @Description("Kiểm tra Meta Title, Meta description có nội dung, để trống, hoặc nhiều dòng đều lưu thành công và form sửa hiện đúng nội dung (giữ đủ các dòng).")
    public void testMetaFieldsKept(String variant, String metaTitle, String metaDescription) {
        tc("AEC_CAT_TC_031", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant);
        String name = "AutoCatMeta_" + newToken("TC031" + variant);
        CategoryListPage list = openCategoriesAsAdmin();

        act("Tạo danh mục với Meta theo biến thể", () -> createCategory(name, form -> {
            if (!metaTitle.isEmpty()) {
                form.enterMetaTitle(metaTitle);
            }
            if (!metaDescription.isEmpty()) {
                form.enterMetaDescription(metaDescription);
            }
        }));

        CategoryFormPage form = openEditOf(list, name);
        verify("Hai ô Meta đúng nội dung", () -> {
            Assert.assertEquals(form.metaTitleValue(), metaTitle, "Meta Title");
            Assert.assertEquals(form.metaDescriptionValue(), metaDescription, "Meta description");
        });
    }

    @DataProvider(name = "attributeChoices", parallel = true)
    public Object[][] attributeChoices() {
        return new Object[][]{{"a", 1, false}, {"b", 2, false}, {"c", 1, true}};
    }

    @Test(dataProvider = "attributeChoices", groups = {"regression", "category", "auth"},
            description = "Chọn nhiều Filtering Attributes và giữ đúng sau khi lưu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ô Filtering Attributes mở danh sách có ô tìm; chọn 1 hoặc 2 thuộc tính đầu thì ô hiện tên đã chọn và form sửa giữ đúng; chọn rồi bỏ chọn thì về Nothing selected.")
    public void testFilteringAttributesKept(String variant, int count, boolean unselect) {
        tc("AEC_CAT_TC_032", "regression", "category", "auth");
        Allure.parameter("Biến thể", variant + " — " + count + " thuộc tính" + (unselect ? " rồi bỏ chọn" : ""));
        String name = "AutoCatAttr_" + newToken("TC032" + variant);
        CategoryListPage list = openCategoriesAsAdmin();
        CategoryFormPage form = new CategoryFormPage();

        verify("Danh sách thuộc tính mở ra, có ô tìm", () -> {
            form.openCreate();
            Assert.assertTrue(form.isAttributesSearchShown(), "Danh sách thuộc tính phải có ô tìm");
        });

        List<String> chosen = act("Chọn " + count + " thuộc tính đầu" + (unselect ? " rồi bấm lại để bỏ" : ""), () -> {
            for (int i = 0; i < count; i++) {
                form.toggleAttribute(i);
            }
            if (unselect) {
                form.toggleAttribute(0);
            }
            form.closeAttributesList();
            return form.selectedAttributes();
        });

        verify("Ô hiện đúng thuộc tính đã chọn", () ->
                Assert.assertEquals(form.attributesShown(), chosen.isEmpty() ? "Nothing selected" : String.join(", ", chosen),
                        "Chữ trên ô Filtering Attributes"));

        act("Nhập Name, Save", () -> {
            form.saveWithName(name);
            Assert.assertEquals(form.getNotifyMessage(), INSERTED, "Lưu phải thành công");
        });

        CategoryFormPage edit = openEditOf(list, name);
        verify("Form sửa giữ đúng thuộc tính", () -> {
            Assert.assertEquals(edit.selectedAttributes(), chosen, "Thuộc tính sau khi lưu");
            if (unselect) {
                Assert.assertEquals(edit.attributesShown(), "Nothing selected", "Chữ trên ô Filtering Attributes");
            }
        });
    }

    @DataProvider(name = "slugNames", parallel = true)
    public Object[][] slugNames() {
        return new Object[][]{{"a", "AutoCatSlug_", "_"}, {"b", "AutoCat @#! ", " "}};
    }

    @Test(dataProvider = "slugNames", groups = {"regression", "category", "auth"}, description = "Slug tự sinh từ Name kèm 5 ký tự ngẫu nhiên")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Slug tự sinh: (a) Name bỏ ký tự đặc biệt kể cả _ + -<5 ký tự>; (b) Name có @#! và dấu cách thì Slug chỉ gồm chữ, số, dấu -.")
    public void testGeneratedSlug(String variant, String head, String separator) {
        tc("AEC_CAT_TC_033", "regression", "category", "auth");
        String token = newToken("TC033" + variant);
        String name = head + token.replace("_", separator);
        Allure.parameter("Biến thể", variant + " — " + name);
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        CategoryFormPage form = openEditOf(list, name);
        verify("Slug đúng dạng", () -> {
            String slug = form.slugValue();
            Assert.assertTrue(slug.matches("^[A-Za-z0-9-]+$"), "Slug chỉ gồm chữ, số, dấu -: " + slug);
            if ("a".equals(variant)) {
                Assert.assertTrue(slug.matches(generatedSlugPattern(name)), "Slug từ Name bỏ ký tự đặc biệt: " + slug);
            }
        });
    }

    @DataProvider(name = "slugEdits", parallel = true)
    public Object[][] slugEdits() {
        return new Object[][]{{"a", true}, {"b", false}};
    }

    @Test(dataProvider = "slugEdits", groups = {"regression", "category", "auth"}, description = "Sửa Slug tuỳ ý, Slug được lưu dạng chữ thường")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra đặt Slug có chữ hoa thì lưu thành chữ thường; xoá trống Slug thì được sinh lại từ Name.")
    public void testEditSlug(String variant, boolean customSlug) {
        tc("AEC_CAT_TC_034", "regression", "category", "auth");
        String token = newToken("TC034" + variant);
        String name = "AutoCat_" + token;
        String slug = customSlug ? "AutoCat-Slug-" + token.replace('_', '-') : "";
        Allure.parameter("Biến thể", variant + " — " + (customSlug ? slug : "để trống"));
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);
        CategoryFormPage form = openEditOf(list, name);

        act("Thay ô Slug rồi bấm Save", () -> form.saveWithSlug(slug));

        verify("Thông báo cập nhật, Slug đúng", () -> {
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Thông báo sau khi sửa");
            if (customSlug) {
                Assert.assertEquals(form.slugValue(), slug.toLowerCase(), "Slug phải được lưu chữ thường");
            } else {
                Assert.assertTrue(form.slugValue().matches(generatedSlugPattern(name)), "Slug sinh lại từ Name: " + form.slugValue());
            }
        });
    }

    @Test(groups = {"regression", "category", "auth", "validation", "knownIssue"}, description = "Không lưu được Slug trùng với danh mục khác")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra đặt cho danh mục B đúng Slug của A rồi Save thì báo lỗi Slug đã tồn tại, Slug của B giữ giá trị cũ. Đang lệch: hệ thống chấp nhận Slug trùng (AMB-CAT-04).")
    public void testDuplicateSlugRejected() {
        tc("AEC_CAT_TC_035", "regression", "category", "auth", "validation", "knownIssue");
        String token = newToken("TC035");
        String nameA = "AutoCatA_" + token;
        String nameB = "AutoCatB_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(nameA);
        createCategory(nameB);

        String slugA = arrange("Ghi lại Slug của A", () -> openEditOf(list, nameA).slugValue());
        CategoryFormPage formB = openEditOf(list, nameB);
        String oldSlugB = formB.slugValue();

        act("Nhập Slug của A vào B rồi bấm Save", () -> formB.saveWithSlug(slugA));

        verify("Không báo cập nhật thành công, Slug của B giữ giá trị cũ", () -> {
            Assert.assertNotEquals(formB.getNotifyMessage(), UPDATED, "Không được chấp nhận Slug trùng");
            Assert.assertEquals(openEditOf(list, nameB).slugValue(), oldSlugB, "Slug của B");
        });
    }

    @Test(groups = {"regression", "category", "auth", "localization"}, description = "Name tiếng Việt lưu riêng ở tab Vietnamese")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể a: ở tab Vietnamese (?lang=vn, ô Name có sẵn tên tiếng Anh) đổi Name rồi Save thì tab English và danh sách vẫn là tên tiếng Anh.")
    public void testVietnameseNameKeptSeparately() {
        tc("AEC_CAT_TC_036", "regression", "category", "auth", "localization");
        Allure.parameter("Biến thể", "a — Name");
        String token = newToken("TC036a");
        String name = "AutoCat_" + token;
        String vietnameseName = "Danh mục thử " + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);
        CategoryFormPage form = openEditOf(list, name);

        act(StepNames.CLICK_VIETNAMESE_TAB, () -> form.clickTab("Vietnamese"));
        verify("?lang=vn, ô Name có sẵn tên tiếng Anh", () -> {
            Assert.assertTrue(form.currentUrl().endsWith("lang=vn"), "Đường dẫn: " + form.currentUrl());
            Assert.assertEquals(form.nameValue(), name, "Ô Name ở tab Vietnamese");
        });

        act("Đổi Name tiếng Việt rồi bấm Save", () -> {
            form.saveWithName(vietnameseName);
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Lưu phải thành công");
        });

        verify("Tab English vẫn là tên tiếng Anh", () -> {
            form.clickTab("English");
            Assert.assertEquals(form.nameValue(), name, "Name ở tab English");
        });
        verify("Danh sách vẫn hiện tên tiếng Anh", () ->
                Assert.assertTrue(list.findRow(name) >= 0, "Danh sách phải hiện tên tiếng Anh"));
    }

    @Test(groups = {"regression", "category", "auth", "localization"}, description = "Meta Title dùng chung hai ngôn ngữ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Biến thể b: ở tab Vietnamese đổi Meta Title rồi Save thì tab English cũng hiện Meta Title đó.")
    public void testMetaTitleSharedAcrossLanguages() {
        tc("AEC_CAT_TC_036", "regression", "category", "auth", "localization");
        Allure.parameter("Biến thể", "b — Meta Title");
        String name = "AutoCat_" + newToken("TC036b");
        String metaTitle = "Meta VN TC036";
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);
        CategoryFormPage form = openEditOf(list, name);

        act("Tab Vietnamese — đổi Meta Title rồi bấm Save", () -> {
            form.clickTab("Vietnamese");
            form.enterMetaTitle(metaTitle);
            form.saveAndWait();
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Lưu phải thành công");
        });

        verify("Tab English hiện cùng Meta Title", () -> {
            form.clickTab("English");
            Assert.assertEquals(form.metaTitleValue(), metaTitle, "Meta Title ở tab English");
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Form sửa không cho chọn chính danh mục hoặc con cháu của nó làm cha")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ở form sửa danh mục cha, ô Parent Category không có chính nó, con, cháu; vẫn có danh mục gốc khác. Không bấm Save (PC-4).")
    public void testParentPickerExcludesSelfAndDescendants() {
        tc("AEC_CAT_TC_037", "regression", "category", "auth");
        String token = newToken("TC037");
        String parent = "AutoCatP_" + token;
        String child = "AutoCatC_" + token;
        String grandchild = "AutoCatG_" + token;
        String other = "AutoCatO_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(parent);
        createChildCategory(child, parent);
        createChildCategory(grandchild, child);
        createCategory(other);
        CategoryFormPage form = openEditOf(list, parent);

        verify("(a)(b)(c) Không có chính nó, con, cháu", () -> {
            for (String excluded : List.of(parent, child, grandchild)) {
                form.searchParent(excluded);
                Assert.assertTrue(form.isParentNoResultsShown(), "Ô Parent Category không được có " + excluded);
            }
        });

        verify("(d) Có danh mục gốc khác", () ->
                Assert.assertEquals(form.searchParent(other), List.of(other), "Ô Parent Category phải có " + other));

        act("Đóng danh sách, không Save", form::closeParentList);
        verify("Danh mục cha không đổi", () -> {
            int row = list.findRow(parent);
            Assert.assertEquals(list.parentOf(row), EMPTY_CELL, "Parent Category của danh mục cha");
            Assert.assertEquals(list.levelOf(row), "0", "Level của danh mục cha");
        });
    }

    @Test(groups = {"regression", "category", "auth"}, description = "Đổi cha ở form sửa tính lại Level")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra (a) đổi Parent Category của B thành A thì danh sách hiện Parent A, Level 1; (b) đổi lại No Parent thì Parent —, Level 0.")
    public void testChangeParentRecalculatesLevel() {
        tc("AEC_CAT_TC_038", "regression", "category", "auth");
        String token = newToken("TC038");
        String nameA = "AutoCatA_" + token;
        String nameB = "AutoCatB_" + token;
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(nameA);
        createCategory(nameB);

        act("(a) Form sửa B — chọn cha A, Save", () -> {
            CategoryFormPage form = openEditOf(list, nameB);
            form.chooseParent(nameA);
            form.saveAndWait();
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Lưu phải thành công");
        });
        verify("(a) B có Parent A, Level 1", () -> {
            int row = list.findRow(nameB);
            Assert.assertEquals(list.parentOf(row), nameA, "Parent Category");
            Assert.assertEquals(list.levelOf(row), "1", "Level");
        });

        act("(b) Form sửa B — chọn No Parent, Save", () -> {
            CategoryFormPage form = openEditOf(list, nameB);
            form.chooseParent(NO_PARENT);
            form.saveAndWait();
            Assert.assertEquals(form.getNotifyMessage(), UPDATED, "Lưu phải thành công");
        });
        verify("(b) B có Parent —, Level 0", () -> {
            int row = list.findRow(nameB);
            Assert.assertEquals(list.parentOf(row), EMPTY_CELL, "Parent Category");
            Assert.assertEquals(list.levelOf(row), "0", "Level");
        });
    }

    @Test(groups = {"regression", "category", "auth", "integration"}, description = "Danh mục chỉ xuất hiện ở form sản phẩm đúng với Type của nó")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra ô Category của Add New Product có danh mục Physical, không có Digital; form Add New Digital Product có Digital, không có Physical. Không bấm Save ở form sản phẩm.")
    public void testCategoryTypeInProductForms() {
        tc("AEC_CAT_TC_039", "regression", "category", "auth", "integration");
        String token = newToken("TC039");
        String physical = "AutoCatPhy_" + token;
        String digital = "AutoCatDig_" + token;
        openCategoriesAsAdmin();
        createCategory(physical);
        createCategory(digital, form -> form.chooseType("Digital"));
        ProductCreatePage productPage = new ProductCreatePage();

        verify("(a)(b) Add New Product có Physical, không có Digital", () ->
                Assert.assertEquals(productPage.open().categoryOptionsContaining(token), List.of(physical), "Ô Category sản phẩm vật lý"));

        verify("(c)(d) Add New Digital Product có Digital, không có Physical", () ->
                Assert.assertEquals(productPage.openDigital().categoryOptionsContaining(token), List.of(digital), "Ô Category sản phẩm số"));
    }

    @DataProvider(name = "unicodeNames", parallel = true)
    public Object[][] unicodeNames() {
        return new Object[][]{
                {"a", "Điện thoại Ưu đãi "},
                {"b", "AutoCat 📱🎧 "},
                {"c", "カテゴリ "}
        };
    }

    @Test(dataProvider = "unicodeNames", groups = {"regression", "category", "auth", "localization"},
            description = "Tên có dấu tiếng Việt, emoji và chữ Nhật hiển thị và tìm được")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra Name tiếng Việt có dấu, emoji, chữ Nhật lưu thành công, tìm bằng toàn bộ Name ra đúng 1 dòng hiện đúng ký tự, form sửa đúng nguyên văn.")
    public void testUnicodeName(String variant, String head) {
        tc("AEC_CAT_TC_048", "regression", "category", "auth", "localization");
        String name = head + newToken("TC048" + variant);
        Allure.parameter("Biến thể", variant + " — " + name);
        CategoryListPage list = openCategoriesAsAdmin();
        createCategory(name);

        verify("Tìm bằng toàn bộ Name ra đúng 1 dòng", () -> {
            list.searchFor(name);
            Assert.assertEquals(list.names(), List.of(name), "Tên ở danh sách");
        });

        CategoryFormPage form = act(StepNames.OPEN_EDIT_FORM, () -> list.editRow(0));
        verify("Ô Name đúng nguyên văn", () -> Assert.assertEquals(form.nameValue(), name, "Ô Name"));
    }
}
