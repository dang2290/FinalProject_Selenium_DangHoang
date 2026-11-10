# Active eCommerce CMS — Selenium Java Automation

Framework kiểm thử tự động cho `https://cms.anhtester.com` (trang quản trị Active eCommerce CMS), viết bằng **Selenium 4 + Java 17 + TestNG + Allure**. Manual test case, requirements và kết quả chạy tay nằm ở `docs/`.

## Yêu cầu máy

| Cần | Ghi chú |
|---|---|
| JDK 17 trở lên | Kiểm tra: `java -version` |
| Google Chrome | Selenium Manager tự tải ChromeDriver khớp bản Chrome — không cài driver tay |
| Maven | **Không bắt buộc** — dùng Maven Wrapper `mvnw` / `mvnw.cmd` có sẵn |

Không cần cài Allure: bộ Allure CLI được Maven giải nén vào `.allure/` ngay trong project.

## Cài đặt

```bash
copy .env.example .env
```

Điền `.env`:

| Khoá | Ý nghĩa | Mặc định |
|---|---|---|
| `BASE_URL` | Địa chỉ site. Ghi kèm đường dẫn (VD `/login`) vẫn được — framework chỉ lấy phần gốc | `https://cms.anhtester.com` |
| `BROWSER` | `chrome` · `edge` · `firefox` | `chrome` |
| `HEADLESS` | `true` để chạy không mở cửa sổ | `false` |
| `TIMEOUT` | Giây chờ tối đa cho mỗi điều kiện | `15` |
| `APP_USERNAME` · `APP_PASSWORD` | Tài khoản admin test | — bắt buộc cho test cần đăng nhập |

Thứ tự ưu tiên: `-DKEY=...` → biến môi trường → `.env`. `.env` nằm trong `.gitignore`, **không** commit.

## Chạy test

```bash
.\mvnw.cmd clean test
```

```bash
./mvnw clean test
```

Chạy riêng một phần (bỏ qua `testng.xml`, nên phải tự truyền số luồng). Trong PowerShell **giữ nguyên dấu nháy** — PowerShell tách tham số ở dấu chấm:

| Muốn chạy | Giá trị `-Dtest` |
|---|---|
| Một test | `"-Dtest=AdminLoginTest#testAdminLoginPageElements"` |
| Nhiều class / module | `"-Dtest=Brand*Test,Category*Test"` — ngăn bằng **dấu phẩy** |
| Nhiều method cùng một class | `"-Dtest=BrandTest#testRenameBrand+testDeleteBrandViaConfirmation"` — dấu `+` chỉ dùng trong một class |

```bash
.\mvnw.cmd test "-Dtest=Brand*Test,Category*Test" "-Dsurefire.failIfNoSpecifiedTests=false" "-Dparallel=methods" "-DthreadCount=5"
```

### Số luồng song song

Suite **luôn chạy song song**, mặc định **5 luồng**. Đổi ở **một chỗ duy nhất**: thuộc tính `thread-count` trong `testng.xml`. Mỗi luồng có trình duyệt riêng (`ThreadLocal`), mỗi test tự tạo dữ liệu riêng — không hạ số luồng để né test đỏ.

## Mở report

```bash
.\mvnw.cmd allure:serve
```

| Việc | Lệnh |
|---|---|
| Sinh report vào `reports/allure-report` | `.\mvnw.cmd allure:report` |
| Gọi thẳng CLI trong project, không qua Maven | `.allure\allure-2.32.0\bin\allure.bat open reports\allure-report` |
| Bản 1 file HTML để gửi người khác | `.allure\allure-2.32.0\bin\allure.bat generate reports\allure-results -o reports\allure-report-single --single-file --clean` |

**Máy không có JDK** thì không chạy được test, nhưng vẫn xem được report:
- Máy có Node.js → `npx allure generate reports/allure-results` rồi `npx allure open reports/allure-report` (giao diện Allure 3)
- Máy không có gì → mở file `reports/allure-report-single/index.html` bằng trình duyệt

⚠️ Report chứa ảnh chụp hệ thống thật — kiểm nội dung trước khi gửi ra ngoài team.

Toàn bộ output nằm trong `reports/` (đã `.gitignore`): `allure-results/`, `allure-report/`, `surefire/`, `logs/`, `screenshots/`.

## Cấu trúc

```text
├── pom.xml · testng.xml · .env.example · mvnw · .github/workflows/selenium.yml
├── src/main/java/com/aec/
│   ├── config/ConfigReader.java       đọc cấu hình
│   ├── drivers/DriverFactory.java     WebDriver theo thread
│   ├── pages/                         Page Object: BasePage, StorefrontPage, LoginPage, CustomerLoginPage,
│   │                                  RegistrationPage, ForgotPasswordPage, DashboardPage, AdminListPage, BrandPage,
│   │                                  BrandEditPage, MediaLibraryModal, ProductCreatePage, BootstrapSelect,
│   │                                  CategoryListPage, CategoryFormPage, AttributeListPage,
│   │                                  AttributeEditPage, AttributeDetailPage, AttributeValueEditPage,
│   │                                  ColorListPage, ColorEditPage, ProductListPage, ProductEditPage,
│   │                                  DigitalProductListPage, DigitalProductFormPage, ProductBulkUploadPage,
│   │                                  StorefrontProductPage, StorefrontListingPage
│   └── utils/                         WaitHelper, TestDataGenerator, ScreenshotUtil, JsonDataReader
├── src/main/resources/log4j2.xml      log ghi vào reports/logs/
├── src/test/java/com/aec/
│   ├── base/BaseTest.java             mở/đóng driver, đính ảnh cuối mọi test, đăng nhập admin
│   └── tests/                         AdminLoginTest, CustomerLoginTest, RegistrationTest,
│                                      ForgotPasswordTest, SessionTest, BrandTestBase,
│                                      BrandTest, BrandEditTest, BrandValidationTest, BrandListTest,
│                                      CategoryTestBase, CategoryTest, CategoryFormTest, CategoryListTest,
│                                      AttributeTestBase, AttributeListTest, AttributeCreateTest,
│                                      AttributeEditTest, AttributeValueTest, AttributeLifecycleTest,
│                                      ColorTestBase, ColorListTest, ColorCreateTest, ColorEditTest,
│                                      ColorLifecycleTest, ProductTestBase, ProductListTest,
│                                      ProductCreateTest, ProductPriceVariantTest, ProductEditTest,
│                                      DigitalBulkProductTest, ProductCrossModuleTest
├── src/test/resources/testdata/       dữ liệu data-driven (JSON)
└── docs/                              requirements, manual TC, kết quả chạy tay
```

## Quy ước

| Hạng mục | Quy ước |
|---|---|
| Page class | `<Tên>Page`, locator `private static final By` trong class, method thao tác có `@Step` tiếng Việt |
| Test class | `<Tên>Test`, method `test<HànhVi>`, assertion chỉ nằm trong test |
| Metadata | `description` = tên test tiếng Việt · `@Description` · `@Severity` · `@Feature`/`@Story` · `Allure.label("testId", "<TC ID trong docs/testcases>")` |
| Thân test | Bọc trong `Allure.step("Arrange: …")` / `Act:` / `Assert:` |
| Chờ | Chỉ `WebDriverWait` + `ExpectedConditions` — cấm `Thread.sleep` |
| Dữ liệu | `TestDataGenerator` sinh tên unique `<Prefix>_<TC>_<epoch>_<hex>` — truy ngược được test nào tạo |
| Môi trường dùng chung | Chỉ tạo, sửa, xoá bản ghi do chính test tạo; dọn ngay trong test |

## Test đã có

| Class | TC ID (`docs/testcases/`) | Cần đăng nhập |
|---|---|---|
| `AdminLoginTest` | `AEC_LOGIN_TC_001, 002, 008, 009, 010a, 034–038` | Không |
| `CustomerLoginTest` | `AEC_LOGIN_TC_003, 021, 022, 024, 038, 040` | Không |
| `RegistrationTest` | `AEC_LOGIN_TC_004, 025, 026` — không tạo tài khoản (026 dùng email admin đã tồn tại) | Không (026 đọc `APP_USERNAME`) |
| `ForgotPasswordTest` | `AEC_LOGIN_TC_005, 029, 030, 033, 039` — không gửi link tới email có thật | Không |
| `SessionTest` | `AEC_LOGIN_TC_013` (không cần) · `006, 007, 014–020` (group `auth`) | Có |
| `BrandTest` · `BrandEditTest` · `BrandValidationTest` · `BrandListTest` | `AEC_BRAND_TC_001–050` trừ 023, 024-a, 036, 048 — chỉ tạo/xoá brand mang mã test, dọn sau mỗi test | Có (trừ TC_046 a, b) |
| `CategoryTest` · `CategoryFormTest` · `CategoryListTest` | `AEC_CAT_TC_001–050` trừ 019, 022, 023-a — chỉ tạo/xoá danh mục mang mã test, dọn cháu → con → cha, không đụng 4541–4543 | Có (trừ TC_020 a, b) |
| `AttributeListTest` · `AttributeCreateTest` · `AttributeEditTest` · `AttributeValueTest` · `AttributeLifecycleTest` | `AEC_ATTR_TC_001–042` trừ 013-a, 015, 029 — chỉ tạo/xoá thuộc tính mang mã test, xoá giá trị trước thuộc tính | Có (trừ TC_035) |
| `ColorListTest` · `ColorCreateTest` · `ColorEditTest` · `ColorLifecycleTest` | `AEC_COLOR_TC_001–049` trừ 037 — chỉ tạo/sửa/xoá màu mang mã test (hoặc id đã ghi), không bấm công tắc Color filter activation, không lưu sản phẩm | Có (trừ TC_043) |
| `ProductListTest` · `ProductCreateTest` · `ProductPriceVariantTest` · `ProductEditTest` · `DigitalBulkProductTest` · `ProductCrossModuleTest` | `AEC_PROD_TC_001–077` trừ 035-c, 064 (076–077: luồng Brand → Category → Product → cửa hàng, dọn sản phẩm → danh mục → brand) — chỉ tạo/sửa/xoá sản phẩm mang mã test; TC_057 chỉ tick 2 dòng test đã kiểm id | Có (trừ TC_068) |

### Group dùng để lọc

| Group | Nghĩa |
|---|---|
| `auth` | Đăng nhập bằng `APP_USERNAME` / `APP_PASSWORD` |
| `knownIssue` | Test **cố ý đỏ** — phơi lỗi đã biết của hệ thống (AMB trong `docs/requirements`). Không sửa kỳ vọng cho xanh |
| `smoke` · `regression` · `security` · `validation` · `responsive` · `accessibility` | Nhóm chạy theo loại kiểm thử |

LOGIN chưa automate: TC_011, 012, 023, 027, 028, 031, 041 (thiếu Chrome thật, tài khoản khách test, quyền tạo tài khoản, hộp thư) và TC_010b, 032 (thử sai mật khẩu với email admin — chạy lặp tự động có thể khoá tài khoản dùng chung).

## CI — GitHub Actions

`.github/workflows/selenium.yml` chạy khi push `main`, khi có pull request, và chạy tay (`workflow_dispatch`): headless, song song theo `testng.xml`, sinh Allure bằng CLI trong project, upload cả thư mục `reports/` (kể cả khi fail).

Khai báo **Secrets** của repo: `BASE_URL`, `APP_USERNAME`, `APP_PASSWORD`. Runner GitHub-hosted chỉ có 2 vCPU — nếu chạy 5 luồng bị chập chờn thì hạ `thread-count` trong `testng.xml`.

## Xử lý sự cố

| Lỗi | Cách xử lý |
|---|---|
| `Unable to establish loopback connection` khi mở trình duyệt | Thư mục tạm của Windows chặn socket nội bộ của Java. Đặt thư mục tạm khác: `set JDK_JAVA_OPTIONS=-Djdk.net.unixdomain.tmpdir=D:\tmp` (thư mục phải tồn tại, đường dẫn ngắn) rồi chạy lại |
| `Thiếu cấu hình APP_USERNAME` | Điền `.env` hoặc biến môi trường |
