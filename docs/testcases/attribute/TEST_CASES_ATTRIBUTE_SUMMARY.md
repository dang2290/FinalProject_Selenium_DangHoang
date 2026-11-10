# Test Cases — Attribute (tổng 42 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_ATTRIBUTE_SUMMARY.md](../../requirements/attribute/REQUIREMENTS_ATTRIBUTE_SUMMARY.md) · web: [requirements_attribute_web.md](../../requirements/attribute/web/requirements_attribute_web.md) |
| Mode · độ hạt | FULL RBT · **GỘP** — 42 TC · 66 ca kiểm (15 TC có bảng biến thể, 39 biến thể) |
| Dải TC ID đã dùng | `AEC_ATTR_TC_001` → `AEC_ATTR_TC_042` — mã kế tiếp `AEC_ATTR_TC_043` |
| Phạm vi REQ | `REQ-ATTR-01` → `REQ-ATTR-34` (34/34). Không có REQ bị loại khỏi phạm vi |
| Mức rủi ro · độ sâu | `Cao` → **Đầy đủ** — xem đầu file web. **Nâng thêm khi:** có tài khoản role khác, hoặc Dev bổ sung giới hạn độ dài giá trị |
| Giả định đang áp | Q1–Q10 (≈ AMB-ATTR-01 → 12) — user chấp nhận toàn bộ 30-10-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Môi trường | **Dùng chung** (user xác nhận 03-11-2026) — chỉ đụng dữ liệu do TC tạo. **TC_029 mỗi lần chạy để lại 1 giá trị mồ côi** không dọn được qua giao diện (AMB-ATTR-06) |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/test_cases_attribute_web.md](web/test_cases_attribute_web.md) | V1 Smoke · V2a Danh sách & tạo · V2b Sửa, giá trị, xoá, liên kết · V3 · V4 | 42 | 001–042 | REQ-ATTR-01 → 34 |

## Bảng Đối Soát Coverage

| REQ ID | Mô tả ngắn | Số TC | TC IDs | Đủ Positive/Negative/Boundary? |
|---|---|---|---|---|
| REQ-ATTR-01 | Mở trang All Attributes | 4 | 001, 004, 035, 041 | ✅ Positive + Negative (chưa đăng nhập) |
| REQ-ATTR-02 | Các cột của bảng | 2 | 001, 009 | ✅ Positive |
| REQ-ATTR-03 | Một trang, không phân trang | 2 | 009, 042 | ✅ Positive + Hiệu năng |
| REQ-ATTR-04 | Mới nằm đầu bảng | 1 | 005 | ✅ Positive |
| REQ-ATTR-05 | Cột Values | 2 | 001, 010 | ✅ Positive (có / không có giá trị) |
| REQ-ATTR-06 | Ba nút thao tác | 2 | 001, 011 | ✅ Positive |
| REQ-ATTR-07 | Form Add New Attribute | 3 | 001, 040, 041 | ✅ Positive + bàn phím |
| REQ-ATTR-08 | Name bắt buộc khi tạo | 1 | 012 | ✅ Negative |
| REQ-ATTR-09 | Tạo thành công | 4 | 005, 032, 033, 034 | ✅ Positive + Negative (double submit, hết phiên) |
| REQ-ATTR-10 | Trùng tên | 1 | 017 | ✅ (`@AssumptionBased`) |
| REQ-ATTR-11 | Bỏ khoảng trắng đầu/cuối | 1 | 016 | ✅ Boundary |
| REQ-ATTR-12 | Name tối đa 50 | 2 | 013, 014 | ✅ Boundary 1/49/50 + 51/255 (`@KnownIssue`) |
| REQ-ATTR-13 | Name toàn khoảng trắng khi tạo | 1 | 015 | ✅ Negative (`@KnownIssue`) |
| REQ-ATTR-14 | Ký tự đặc biệt hiển thị như chữ | 1 | 036 | ✅ Negative (bảo mật) |
| REQ-ATTR-15 | Unicode trong Name | 2 | 018, 039 | ✅ Positive + emoji (`@KnownIssue`) |
| REQ-ATTR-16 | Trang sửa | 2 | 002, 035 | ✅ Positive + Negative |
| REQ-ATTR-17 | Lưu sửa Name | 3 | 006, 032, 034 | ✅ Positive + Negative (hết phiên) |
| REQ-ATTR-18 | Name bắt buộc khi sửa | 1 | 019 | ✅ Negative |
| REQ-ATTR-19 | Tên tiếng Việt lưu riêng | 1 | 021 | ✅ Positive |
| REQ-ATTR-20 | Sửa Name toàn khoảng trắng | 1 | 020 | ✅ Negative (`@KnownIssue`) |
| REQ-ATTR-21 | Trang Attribute Detail | 2 | 003, 035 | ✅ Positive + Negative |
| REQ-ATTR-22 | Giá trị bắt buộc | 1 | 022 | ✅ Negative |
| REQ-ATTR-23 | Thêm giá trị | 3 | 007, 032, 037 | ✅ Positive + Negative (bảo mật) |
| REQ-ATTR-24 | Giá trị trùng | 1 | 023 | ✅ (`@AssumptionBased`) |
| REQ-ATTR-25 | Giá trị toàn khoảng trắng | 1 | 024 | ✅ Negative (`@KnownIssue`) |
| REQ-ATTR-26 | Độ dài giá trị | 1 | 025 | ✅ Boundary 51 / 256 |
| REQ-ATTR-27 | Nút Edit/Delete của giá trị | 2 | 007, 026 | ✅ Positive |
| REQ-ATTR-28 | Sửa giá trị | 1 | 026 | ✅ Positive |
| REQ-ATTR-29 | Xoá giá trị | 2 | 027, 032 | ✅ Positive + Cancel |
| REQ-ATTR-30 | Giá trị dài không vỡ bố cục | 1 | 038 | ✅ Boundary (`@KnownIssue`) |
| REQ-ATTR-31 | Hộp xác nhận xoá thuộc tính | 3 | 008, 028, 040 | ✅ Positive + Cancel + Esc |
| REQ-ATTR-32 | Xoá thành công | 2 | 008, 032 | ✅ Positive |
| REQ-ATTR-33 | Xoá kèm giá trị con | 1 | 029 | ✅ Negative (`@KnownIssue`) |
| REQ-ATTR-34 | Nguồn của Category / Product | 2 | 030, 031 | ✅ Positive + xoá khi đang dùng (`@AssumptionBased` — AMB-ATTR-07) |

**34/34 REQ có TC.** Phép thử 6b (TC gánh ≥ 2 REQ không có TC khác chống lưng): **không vi phạm** — TC_026 ban đầu là TC duy nhất của REQ-ATTR-27 và 28, đã gắn REQ-ATTR-27 vào TC_007 (Expected bước 4 kiểm 2 nút `Edit` / `Delete` của giá trị).

## Bảng Đối Soát Evidence

| Ảnh evidence | Màn hình / trạng thái | TC dựa vào | Đầy đủ? |
|---|---|---|---|
| `attribute_list_default_viewport.jpg` | All Attributes — mặc định | 001, 004, 005, 009, 010, 011, 012 | ✅ Viewport đủ bảng + form. Cột `#` trong ảnh mờ trông như `1, 3, 2` — DOM đánh số liên tục, TC theo DOM |
| `attribute_edit_en_default_viewport.jpg` | Attribute Information — tab English | 002, 006, 019, 020, 021 | ✅ |
| `attribute_values_empty_viewport.jpg` | Attribute Detail — chưa có giá trị | 003, 007, 022 | ✅ |
| `attribute_values_long_value_overflow_viewport.jpg` | Attribute Detail — giá trị 256 ký tự | 025, 038 | ✅ |
| `attribute_list_long_value_scroll_viewport.jpg` | All Attributes — giá trị 256 ký tự | 038 | ✅ |
| (không có ảnh — đọc DOM khi recon) | Hộp `Delete Confirmation` · trang sửa giá trị · tab Vietnamese | 008, 026, 027, 028, 021 | ✅ Câu chữ đọc nguyên văn từ DOM |

### Vùng chưa có evidence — TC gắn `@NeedsVerify`

Không còn. 5 vùng ban đầu (TC_019, 030, 031b, 040c, 041b) đã quan sát thật ở [run_1793348598](../../executions/attribute/web/run_1793348598/execution_report.md) — gỡ `@NeedsVerify` 30-10-2026 theo [review](review/testcase_review_report_web_20261030.md).

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC ID / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | TC_001–003 (3 TC · bảng kiểm 6 + 4 + 3 mục) |
| 1 | Open form | ✅ | TC_004 (1 TC · 4 biến thể) |
| 1 | Display | ✅ | TC_001, 005, 007 |
| 1 | Input valid · Save · Verify data | ✅ | TC_005–008 (4 TC) |
| 2 | UI Behavior | ✅ | TC_011 (3 biến thể) · TC_027 (2 biến thể) |
| 2 | Required | ✅ | TC_012, 019, 022 |
| 2 | Validation | ✅ | TC_013–016, 018, 020, 024, 025 (8 TC · 13 biến thể). Bảng Text: đủ 8/8 mục — required · min/max (013, 014, 025) · whitespace-only (015, 020, 024) · ký tự đặc biệt (018) · XSS & SQL (036, 037 ở V3) · Unicode/Emoji (018, 039 ở V4) · khoảng trắng đầu/cuối (016) |
| 2 | Equivalence Partitioning | ✅ | TC_013, 014 (hợp lệ / quá dài) · 018 (chữ có dấu / ký tự đặc biệt / số) |
| 2 | Boundary Value Analysis | ✅ | TC_013 (1 · 49 · 50) · 014 (51 · 255) · 025 (51 · 256) |
| 2 | Business Rule | ✅ | TC_017 (trùng tên) · 023 (trùng giá trị) · 021 (bản dịch riêng) |
| 2 | Decision Table | ➖ | Không có tổ hợp ≥ 3 điều kiện cho một kết quả |
| 2 | State Transition | ➖ | Thuộc tính / giá trị không có trạng thái |
| 2 | Dependency | ✅ | TC_029 (giá trị con) · 030, 031 (Category · Product) |
| 2 | Use Case / Scenario | ✅ | TC_032 |
| 2 | Save / Edit / Delete | ✅ | TC_006, 008, 026, 027, 028, 029 |
| 2 | Error Guessing | ✅ | TC_033 (Save hai lần) · 034 (hết phiên, 2 biến thể) |
| 3 | Permission | ✅ + ⏭️ | TC_035 (chưa đăng nhập, 3 biến thể). Role khác: ⏭️ chưa có tài khoản (AMB-ATTR-10). **Quyết định: user, 30-10-2026 (Q9).** Rà lại khi được cấp tài khoản role khác |
| 3 | Security | ✅ | TC_035, 036, 037 (3 TC · 9 biến thể) |
| 3 | API | ➖ | QA không có quyền gọi API — đội Dev xác minh (danh mục requirements) |
| 3 | Database | ➖ | QA không có quyền truy vấn CSDL — đội Dev xác minh. Giá trị mồ côi của TC_029 cần Dev dọn |
| 3 | Integration | ➖ | Không có tích hợp bên thứ ba — liên kết Category/Product kiểm ở V2 · Dependency |
| 3 | Logging / Audit | ➖ | QA chưa có quyền xem nhật ký; requirements không có yêu cầu audit |
| 4 | Compatibility | ⏭️ Cố ý bỏ | Chỉ kiểm Chromium, giống BRAND và CAT. **Quyết định: user, 30-10-2026 (duyệt Bước 4).** Rà lại khi hệ thống cam kết hỗ trợ trình duyệt khác |
| 4 | Responsive / UI Stability | ✅ | TC_038 (giá trị dài) · TC_041 (1920 · 1366) |
| 4 | Accessibility | ✅ | TC_040 (3 biến thể) |
| 4 | Performance | 🟡 | TC_042 (mức quan sát thô). Ngưỡng 3 giây là giả định, chạy thật ≈ 4 giây — chờ PO chốt ngưỡng (review 30-10-2026). Đo tải thật ➖ — đội hạ tầng |
| 4 | Regression | ➖ | Module mới, chưa có bug nào được đóng |
| 4 | E2E | ✅ | TC_031 (thuộc tính → Category, Product) |

## Đối soát cột Automation

| Kết quả | Số TC | TC |
|---|---|---|
| Yes | 37 | Còn lại |
| Partial | 5 | 030 · 031 · 038 · 041 · 042 |
| No | 0 | — |

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_ATTR_TC_030 | Web | Partial | 2 · dữ liệu chéo module | Cần tạo danh mục test có `Filtering Attributes` — làm được khi có Page Object Category |
| AEC_ATTR_TC_031 | Web | Partial | 2 · dữ liệu chéo module | Biến thể `b` cần Page Object Product (khối `Product Variation`) |
| AEC_ATTR_TC_038 | Web | Partial | 2 · Expected cần mắt người | Tự động: có thanh cuộn ngang, nút `Action` có bị che (điểm bấm trúng nút). Kiểm tay: chữ xuống dòng, bố cục nhìn hợp lý |
| AEC_ATTR_TC_041 | Web | Partial | 2 · Expected cần mắt người | Tự động: không cuộn ngang, nút nhìn thấy. Kiểm tay: chữ không đè nhau |
| AEC_ATTR_TC_042 | Web | Partial | 2 · không có SLA | Ngưỡng 3 giây là giả định; kết quả phụ thuộc mạng |
| AEC_ATTR_TC_042 | Web | ⏸️ Hoãn | — | Chờ PO chốt ngưỡng thời gian tải. (TC_019, 030, 031, 040, 041 hết hoãn — Expected đã chốt qua lần chạy 30-10-2026) |

**Lưu ý automation:** Delete là liên kết GET (`/admin/attributes/destroy/<id>`, `/admin/destroy-attribute-value/<id>`) — automation **chỉ** xoá qua hộp xác nhận, không mở thẳng đường dẫn. Dọn giá trị **trước** khi xoá thuộc tính (PC-3) để không sinh thêm dữ liệu mồ côi; riêng TC_029 cố ý để lại 1 giá trị.

**Thứ tự ưu tiên automate:** `@Smoke`/`@CriticalPath` (TC_001–008, 026, 027) → Validation/BVA (TC_012–016, 022–025) → phần còn lại.

### Automation đã có (04-11-2026)

Selenium Java · `src/test/java/com/aec/tests/` — `AttributeListTest` · `AttributeCreateTest` · `AttributeEditTest` · `AttributeValueTest` · `AttributeLifecycleTest` (nền `AttributeTestBase`). Label `testId` = TC ID. Mỗi thuộc tính test mang mã `TC<nnn><biến thể>_<epoch>_<hex>`, tên ≤ 50 ký tự; dọn sau mỗi test: giá trị trước, thuộc tính sau, rồi danh mục test (TC_030).

| Phạm vi | TC |
|---|---|
| Đã automate | 001–012, 013 (`b`, `c`), 014, 016–028, 030–042 — **40 TC** |
| Không automate | **013-a** (Name 1 ký tự `Q`) và **015** (Name toàn khoảng trắng tạo bản ghi tên rỗng) — không truy vết được để dọn khi nhiều test chạy song song · **029** (cố ý để lại giá trị mồ côi, không dọn được qua giao diện — mỗi lần chạy sinh thêm rác) |
| Lệch nhỏ so với TC | TC_005, 040-a: thuộc tính mới kiểm trong 5 dòng đầu thay vì đúng dòng 1 (5 luồng song song cùng tạo thuộc tính) · TC_012: không đếm số dòng trước/sau (lý do như trên), kiểm form không gửi + bong bóng nhắc · TC_014: kiểm "không có dòng nào chứa mã test" thay vì đọc thông báo lỗi · TC_020: đặt lại tên cũ trước khi dọn nếu tên đã bị lưu rỗng · TC_025: mở trang sửa giá trị bằng đường dẫn của nút Edit (giá trị dài có thể che nút — TC_038) · TC_031: gõ **tên đầy đủ** vào ô chọn · TC_035 b/c: dùng id `1` (chưa đăng nhập không tạo được thuộc tính) |

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–034 |
| Performance Efficiency | ✅ Có TC (mức thô) | TC_042. Đo tải thật ➖ — đội hạ tầng |
| Compatibility | ➖ Ngoài phạm vi | Chỉ Chromium — user quyết 30-10-2026; liên kết dữ liệu với Category/Product đã ở TC_030, 031 |
| Interaction Capability | ✅ Có TC | TC_012, 019, 022 (chặn nhập trống) · 028, 027a (huỷ) · 040 (bàn phím) |
| Reliability | ✅ Có TC | TC_033 (Save hai lần) · 034 (hết phiên) |
| Security | ✅ Có TC | TC_035–037 |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn, không kiểm bằng manual TC |
| Flexibility | ✅ Có TC | TC_021 (đa ngôn ngữ) · 041 (1920 · 1366) |
| Safety | ✅ Có TC | TC_008, 028 (xác nhận trước khi xoá) · 029 (hệ quả xoá với dữ liệu con) |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 4 biến thể/TC (TC_004); TC thiết kế để FAIL (014, 015, 020, 024, 029, 038, 039) tách riêng, mỗi TC một lỗi |
| 1 | Unique TC ID | ✅ 42 ID, không trùng, đúng mẫu `AEC_ATTR_TC_<3 số>` |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected đánh số theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Mọi dữ liệu có giá trị cụ thể, `<ts>` có ví dụ `1793400000`; độ dài ghi rõ số ký tự |
| 4 | Field validation coverage | ✅ 2 trường Text (Name, Attribute Value) — đủ 8/8 mục bảng Text, chi tiết ở dòng `V2 · Validation` |
| 5 | Automation metadata | ✅ 42/42 có `Automation` · `Auto Type` · tag; 5 Partial có lý do ở mục Đối soát cột Automation |
| 6 | Requirement coverage | ✅ 34/34 REQ; 6b đạt (gắn REQ-ATTR-27 vào TC_007) |
| 7 | Evidence-verified | ✅ Đã mở 5/5 ảnh; 5 TC từng gắn `@NeedsVerify` đã có kết quả chạy thật, gỡ tag 30-10-2026 |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không có thuật ngữ DOM ở phần TC chính; mã 500 nằm dưới `🔧` và TC_015 mang `@TechCheck`. `404` và `419 \| PAGE EXPIRED` là chữ hiển thị trên trang |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm, ô `➖` có lý do và người chịu trách nhiệm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴; `UI cơ bản` · `Validation` · `Permission` đều ✅ |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 30-10-2026 | `/generate-testcases-manual-rbt attribute` (FULL RBT) | AEC_ATTR_TC_001 → 042 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-ATTR-01 → 34, độ hạt GỘP, rủi ro Cao, giả định Q1–Q10 |
| 30-10-2026 | `/review-testcases attribute` mode FIX — [báo cáo](review/testcase_review_report_web_20261030.md) | AEC_ATTR_TC_019, 030, 031, 040, 041 | 🟡 Sửa | TC_030: thay Expected hai nhánh bằng hành vi đã quan sát (xoá không bị chặn, ô `Filtering Attributes` về `Nothing selected`), giữ `@AssumptionBased` theo AMB-ATTR-07 · TC_041: thêm Pre-Condition dọn giá trị dài · gỡ `@NeedsVerify` + ghi chú ⚠️ ở 5 TC. TC_025, 042 giữ nguyên chờ requirements / PO. **Mốc trước khi sửa:** dự án chưa có git — bản cũ sao lưu ở thư mục tạm của phiên (`backup_attr_review_20261030`) |
| 04-11-2026 | `/generate-automation-web` mode TC | 40 TC (xem mục Automation đã có) | 🟢 Thêm | Sinh automation Selenium Java. Không đổi nội dung TC nào |
