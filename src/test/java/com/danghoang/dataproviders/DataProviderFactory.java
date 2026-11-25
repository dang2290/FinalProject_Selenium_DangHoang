package com.danghoang.dataproviders;

import com.danghoang.helpers.ExcelHelper;
import com.danghoang.utils.JsonDataReader;
import org.testng.annotations.DataProvider;

/**
 * DataProvider dùng chung nhiều test, và mọi DataProvider đọc file (Excel, JSON).
 * Bộ dữ liệu chỉ một test dùng thì vẫn để cạnh test đó cho dễ đọc.
 * Test gọi: {@code @Test(dataProvider = "windowSizes", dataProviderClass = DataProviderFactory.class)}.
 */
public final class DataProviderFactory {

    private static final String EXCEL = "testdata/test_data.xlsx";
    /** Ô EXPECTED_LEVEL ghi BLOCKED: trình duyệt chặn, không có giá trị được lưu. */
    private static final String BLOCKED = "BLOCKED";

    private DataProviderFactory() {}

    // ---------- Từ file ----------

    @DataProvider(name = "orderLevels", parallel = true)
    public static Object[][] orderLevels() {
        return ExcelHelper.readRows(EXCEL, "CategoryOrderLevel").stream()
                .map(row -> new Object[]{row.get("VARIANT"), row.get("INPUT"),
                        BLOCKED.equals(row.get("EXPECTED_LEVEL")) ? null : row.get("EXPECTED_LEVEL")})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "invalidEmails", parallel = true)
    public static Object[][] invalidEmails() {
        return JsonDataReader.readList("testdata/login_email_invalid.json").stream()
                .map(row -> new Object[]{row})
                .toArray(Object[][]::new);
    }

    // ---------- Dùng chung ----------

    @DataProvider(name = "windowSizes", parallel = true)
    public static Object[][] windowSizes() {
        return new Object[][]{{"a", 1920, 1080}, {"b", 1366, 768}};
    }

    @DataProvider(name = "expiredSessionForms", parallel = true)
    public static Object[][] expiredSessionForms() {
        return new Object[][]{{"a", "create"}, {"b", "edit"}};
    }

    @DataProvider(name = "injectionKeywords", parallel = true)
    public static Object[][] injectionKeywords() {
        return new Object[][]{{"a", "<script>alert(1)</script>zzq"}, {"b", "' OR '1'='1 zzq"}};
    }

    @DataProvider(name = "injectionPayloads", parallel = true)
    public static Object[][] injectionPayloads() {
        return new Object[][]{{"a", "<script>alert(1)</script>"}, {"b", "<img src=x onerror=alert(1)>"}, {"c", "' OR '1'='1 -- "}};
    }

    @DataProvider(name = "nameLengthsWithin50", parallel = true)
    public static Object[][] nameLengthsWithin50() {
        return new Object[][]{{"b", 49}, {"c", 50}};
    }

    @DataProvider(name = "nameLengthsOver50", parallel = true)
    public static Object[][] nameLengthsOver50() {
        return new Object[][]{{"a", 51}, {"b", 255}};
    }

    @DataProvider(name = "languageTabs", parallel = true)
    public static Object[][] languageTabs() {
        return new Object[][]{{"a", "English"}, {"b", "Vietnamese"}};
    }
}
