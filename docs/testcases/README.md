# Danh mục Test Cases — Active eCommerce CMS

| Thuộc tính | Giá trị |
|---|---|
| Hệ thống | Active eCommerce CMS |
| Tiền tố TC ID | `AEC_<MODULE>_TC_<3 số>` — user xác nhận 27-10-2026 |
| Nguồn requirements | [../requirements/README.md](../requirements/README.md) |

## 1. Danh mục module

| Module | Prefix TC ID | Nền tảng | Số TC | Số part | REQ bao phủ | Độ hạt | Index | Cập nhật |
|---|---|---|---|---|---|---|---|---|
| Manage Brand | `AEC_BRAND_TC_` (001 → 050, kế tiếp 051) | Web 50 | 50 · 99 ca | 0 | REQ-BRAND-01 → 28 (28/28) | GỘP | [brand/TEST_CASES_BRAND_SUMMARY.md](brand/TEST_CASES_BRAND_SUMMARY.md) | 27-10-2026 |
| Manage Category | `AEC_CAT_TC_` (001 → 050, kế tiếp 051) | Web 50 | 50 · 113 ca | 0 | REQ-CAT-01 → 35 (35/35) | GỘP | [category/TEST_CASES_CATEGORY_SUMMARY.md](category/TEST_CASES_CATEGORY_SUMMARY.md) | 29-10-2026 |
| Attribute | `AEC_ATTR_TC_` (001 → 042, kế tiếp 043) | Web 42 | 42 · 66 ca | 0 | REQ-ATTR-01 → 34 (34/34) | GỘP | [attribute/TEST_CASES_ATTRIBUTE_SUMMARY.md](attribute/TEST_CASES_ATTRIBUTE_SUMMARY.md) | 30-10-2026 |
| Colors | `AEC_COLOR_TC_` (001 → 049, kế tiếp 050) | Web 49 | 49 · 69 ca | 0 | REQ-COLOR-01 → 30 (30/30) | GỘP | [colors/TEST_CASES_COLORS_SUMMARY.md](colors/TEST_CASES_COLORS_SUMMARY.md) | 02-11-2026 |
| Manage Product | `AEC_PROD_TC_` (001 → 077, kế tiếp 078) | Web 77 | 77 · 103 ca | 3 | REQ-PROD-01 → 60 (60/60) | GỘP | [product/TEST_CASES_PRODUCT_SUMMARY.md](product/TEST_CASES_PRODUCT_SUMMARY.md) | 02-11-2026 |
| Product Reviews | `AEC_REVIEW_TC_` (001 → 019, kế tiếp 020) | Web 19 | 19 · 28 ca | 0 | REQ-REVIEW-01 → 15 (15/15) | GỘP | [review/TEST_CASES_REVIEW_SUMMARY.md](review/TEST_CASES_REVIEW_SUMMARY.md) | 03-11-2026 |
| Đăng nhập · Tài khoản | `AEC_LOGIN_TC_` (001 → 041, kế tiếp 042) | Web 41 | 41 · 59 ca | 0 | REQ-LOGIN-01 → 35 (35/35) | GỘP | [login/TEST_CASES_LOGIN_SUMMARY.md](login/TEST_CASES_LOGIN_SUMMARY.md) | 03-11-2026 |

## 2. Độ phủ so với requirements

| Module | Tổng REQ | REQ có TC | REQ thiếu TC | Ghi chú |
|---|---|---|---|---|
| BRAND | 28 | 28 | 0 | 6 TC `@NeedsVerify`, 4 TC `@KnownIssue` (014, 015 — AMB-BRAND-01; 034, 041 — ESC) |
| CAT | 35 | 35 | 0 | 2 TC `@NeedsVerify` (018, 019 — chờ AMB-CAT-05), 3 TC `@KnownIssue` (022 — AMB-CAT-02; 024 — AMB-CAT-01; 035 — AMB-CAT-04) |
| ATTR | 34 | 34 | 0 | 0 TC `@NeedsVerify` (gỡ 30-10-2026 sau lần chạy), 7 TC `@KnownIssue` (014 — AMB-ATTR-01; 015, 020 — AMB-ATTR-02; 024 — AMB-ATTR-04; 029 — AMB-ATTR-06; 038 — AMB-ATTR-11; 039 — AMB-ATTR-09) |
| COLOR | 30 | 30 | 0 | 0 TC `@NeedsVerify` (gỡ 02-11-2026 sau lần chạy), 7 TC `@KnownIssue` (016, 033 — AMB-COLOR-01; 025 — chờ mở AMB; 028 — AMB-COLOR-03; 030 — AMB-COLOR-04; 046 — AMB-COLOR-05; 047 — AMB-COLOR-07) |
| PROD | 60 | 60 | 0 | 2 TC `@NeedsVerify` (035c — chưa có PDF, 064 — chờ phép tải tệp; gỡ 6 TC 03-11-2026), 15 TC `@KnownIssue` (017, 025, 028, 029, 041, 044, 045, 046, 055, 057, 058, 059, 061, 071, 072c) |
| REVIEW | 15 | 15 | 0 | 5 TC `@NeedsVerify` (008, 009, 011, 012, 019 — chờ dữ liệu đánh giá, AMB-REVIEW-02; gỡ 006, 018 03-11-2026), 2 TC `@KnownIssue` (007 — AMB-REVIEW-01; 010 — AMB-REVIEW-03) |
| LOGIN | 35 | 35 | 0 | 7 TC `@NeedsVerify` (011, 012, 023, 027, 028, 031, 041 — cần Chrome thật, tài khoản khách test, quyền tạo tài khoản, hộp thư; gỡ 035–038 03-11-2026), 9 TC `@KnownIssue` (014, 019, 021, 029, 030, 033, 037a, 039, 040) |

## 3. Cấu trúc thư mục chuẩn

```text
docs/testcases/
├── README.md
└── <module>/
    ├── TEST_CASES_<TÊN_MODULE>_SUMMARY.md   ← index, không chứa dòng TC
    ├── web/test_cases_<module>_web.md
    ├── mobile/test_cases_<module>_mobile.md
    └── api/test_cases_<module>_api.md
```

## 4. Kết quả thực thi

| Module | Nền tảng | Lần chạy | Ngày | Kết quả |
|---|---|---|---|---|
| Đăng nhập · Tài khoản | web | [run_1793688841](../executions/login/web/run_1793688841/execution_report.md) | 03-11-2026 | 41 TC: PASS 25 · FAIL 9 · BLOCKED 7 · SKIPPED 0 — pass rate 61,0% (73,5% không tính BLOCKED) |
| Product Reviews | web | [run_1793676756](../executions/review/web/run_1793676756/execution_report.md) | 03-11-2026 | 19 TC: PASS 12 · FAIL 2 · BLOCKED 5 · SKIPPED 0 — pass rate 63,2% (85,7% không tính BLOCKED) |
| Manage Product | web | [run_1793607938](../executions/product/web/run_1793607938/execution_report.md) | 02-11-2026 → 03-11-2026 | 75 TC: PASS 57 · FAIL 15 · BLOCKED 3 · SKIPPED 0 — pass rate 76,0% (79,2% không tính BLOCKED) |
| Colors | web | [run_1793589946](../executions/colors/web/run_1793589946/execution_report.md) | 02-11-2026 | 49 TC: PASS 41 · FAIL 8 · BLOCKED 0 · SKIPPED 0 — pass rate 83,7% |
| Attribute | web | [run_1793348598](../executions/attribute/web/run_1793348598/execution_report.md) | 30-10-2026 | 42 TC: PASS 31 · FAIL 11 · BLOCKED 0 · SKIPPED 0 — pass rate 73,8% |
| Manage Brand | web | [run_1793105365](../executions/brand/web/run_1793105365/execution_report.md) | 27-10-2026 | 50 TC: PASS 38 · FAIL 10 · SKIPPED 2 — pass rate 79,2% (không tính SKIPPED) |
| Manage Category | web | [run_1793267083](../executions/category/web/run_1793267083/execution_report.md) | 29-10-2026 | Chạy lại TC_003, 028f, 033, 042 sau `/review-testcases` FIX: PASS 3 · FAIL 1 (042b — `403`) |
| Manage Category | web | [run_1793261923](../executions/category/web/run_1793261923/execution_report.md) | 29-10-2026 | 50 TC: PASS 38 · FAIL 11 · BLOCKED 1 · SKIPPED 0 — pass rate 76,0% |
| Manage Brand | web | [run_1793109740](../executions/brand/web/run_1793109740/execution_report.md) | 27-10-2026 | Chạy lại TC_008, 034, 041 sau `/review-testcases` FIX: PASS 1 · FAIL 2 (`@KnownIssue` ESC) |

## 5. Quy trình sử dụng

| Việc | Lệnh |
|---|---|
| Chạy tay bộ TC | `/execute-test-cases brand` |
| Review chất lượng TC | `/review-testcases brand` |
| Sinh automation Selenium Java | `/generate-automation-from-testcases brand` |
| Ma trận truy vết | `/generate-traceability-matrix brand` |

## 6. Nhật ký danh mục

| Ngày | Thay đổi |
|---|---|
| 27-10-2026 | Khởi tạo danh mục. Thêm module Manage Brand: 50 TC web (FULL RBT, độ hạt GỘP). |
| 27-10-2026 | Thêm kết quả thực thi `run_1793105365` của Brand web. |
| 27-10-2026 | Brand: `/review-testcases` mode FIX — sửa TC_008, 034, 041, gỡ `@NeedsVerify` 14 TC ([báo cáo](brand/review/testcase_review_report_web_20261027.md)). |
| 29-10-2026 | Thêm module Manage Category: 50 TC web · 113 ca (FULL RBT, độ hạt GỘP, rủi ro Cao). |
| 29-10-2026 | Thêm kết quả thực thi `run_1793261923` của Category web. |
| 29-10-2026 | Category: `/review-testcases` mode FIX — sửa TC_003, 028, 033, 034, 042, 008, 044, gỡ `@NeedsVerify` 6 TC ([báo cáo](category/review/testcase_review_report_web_20261029.md)). |
| 29-10-2026 | Thêm kết quả chạy lại `run_1793267083` của Category web. |
| 30-10-2026 | Thêm module Attribute: 42 TC web · 66 ca (FULL RBT, độ hạt GỘP, rủi ro Cao). |
| 02-11-2026 | Thêm module Manage Product: 75 TC web · 101 ca, 3 part (FULL RBT, độ hạt GỘP, rủi ro Cao). |
| 02-11-2026 | Thêm module Colors: 49 TC web · 69 ca (FULL RBT, độ hạt GỘP, rủi ro Cao). |
| 02-11-2026 | Thêm kết quả thực thi `run_1793589946` của Colors web. |
| 02-11-2026 | Colors: `/review-testcases` mode FIX — sửa TC_037, đổi tag TC_025, gỡ `@NeedsVerify` 5 TC ([báo cáo](colors/review/testcase_review_report_web_20261102.md)). |
| 30-10-2026 | Thêm kết quả thực thi `run_1793348598` của Attribute web. |
| 30-10-2026 | Attribute: `/review-testcases` mode FIX — sửa TC_030, 041, gỡ `@NeedsVerify` 5 TC ([báo cáo](attribute/review/testcase_review_report_web_20261030.md)). |
| 03-11-2026 | Thêm kết quả thực thi `run_1793607938` của Manage Product web. |
| 03-11-2026 | Manage Product: `/review-testcases` mode FIX — sửa TC_035, 052, 064, 067, thêm `@KnownIssue` TC_025, 072, gỡ `@NeedsVerify` 6 TC ([báo cáo](product/review/testcase_review_report_web_20261103.md)). |
| 03-11-2026 | User xác nhận môi trường **dùng chung**: sửa dòng Môi trường ở index + file TC của Attribute, Category, Colors, Product (Brand vốn ghi dùng chung). |
| 03-11-2026 | Thêm module Product Reviews: 19 TC web · 28 ca (FULL RBT, độ hạt GỘP, rủi ro Trung bình). |
| 03-11-2026 | Thêm kết quả thực thi `run_1793676756` của Product Reviews web. |
| 03-11-2026 | Product Reviews: `/review-testcases` mode FIX — sửa TC_013, 010, gỡ `@NeedsVerify` TC_006, 018 ([báo cáo](review/review/testcase_review_report_web_20261103.md)). |
| 03-11-2026 | Thêm module Đăng nhập · Tài khoản: 41 TC web · 59 ca (FULL RBT, độ hạt GỘP, rủi ro Cao). |
| 03-11-2026 | Thêm kết quả thực thi `run_1793688841` của Đăng nhập · Tài khoản. |
| 03-11-2026 | Đăng nhập · Tài khoản: `/review-testcases` mode FIX — sửa PC-6, TC_024, 010/032, 011, ghi chú 019/020/033, TC_037 thêm `@KnownIssue`, gỡ `@NeedsVerify` 4 TC ([báo cáo](login/review/testcase_review_report_web_20261103.md)). |
| 10-11-2026 | Manage Product: bổ sung TC_076 (luồng Login → Brand → Category → Product), TC_077 (đối chiếu chi tiết Admin ↔ cửa hàng), tiền đề PC-6 — tổng 77 TC. |
