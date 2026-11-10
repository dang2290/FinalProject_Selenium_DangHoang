# Test Cases — Manage Category (tổng 50 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_CATEGORY_SUMMARY.md](../../requirements/category/REQUIREMENTS_CATEGORY_SUMMARY.md) · [web/requirements_category_web.md](../../requirements/category/web/requirements_category_web.md) |
| Dải TC ID đã dùng | `AEC_CAT_TC_001` → `AEC_CAT_TC_050` — chung mọi nền tảng. TC kế tiếp: `AEC_CAT_TC_051` |
| Mode · độ hạt | FULL RBT · **GỘP** — 50 TC · 113 ca kiểm (biến thể `a`, `b`… tính riêng; TC không có biến thể tính 1; tối đa 6 biến thể/TC) |
| Mức rủi ro · độ sâu | Module `Cao` → **Đầy đủ**. Căn cứ: máy chủ không chặn vòng lặp cha–con và vòng lặp làm treo ≈ 3 phút (RISK-CAT-02); tên bị cắt im lặng / tên toàn khoảng trắng gây lỗi 500; chưa quan sát được kết quả xoá; danh mục là trường bắt buộc của Product.<br>**Nâng thêm khi:** có tài khoản role khác, hoặc Dev bổ sung kiểm tra vòng lặp phía máy chủ |
| Phạm vi REQ | `REQ-CAT-01` → `REQ-CAT-35` (35/35). Không REQ nào bị loại khỏi phạm vi |
| Giả định đang áp | Q1–Q10 (≈ AMB-CAT-01 → 12) — user chấp nhận toàn bộ assumption ngày 29-10-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Môi trường | **Dùng chung** (user xác nhận 03-11-2026; 28-10-2026 → 03-11-2026 ghi không dùng chung). TC chỉ đụng danh mục do chính nó tạo; **cấm** đụng `4541`–`4543` (PC-4) |
| Ngày sinh | 29-10-2026 |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/test_cases_category_web.md](web/test_cases_category_web.md) | V1 Smoke · V2a Danh sách/Tìm kiếm/Featured/Xoá/Phân quyền · V2b Form tạo-sửa/Validation/Slug/Đa ngôn ngữ/Product · V3 Technical · V4 Non-functional · V2 bổ sung (TC_050) | 50 | 001–050 | REQ-CAT-01 → 35 |

## Bảng Đối Soát Coverage (toàn module)

| REQ ID | Mô tả ngắn | Web — số TC | TC IDs | Đủ Positive/Negative/Boundary? |
|---|---|---|---|---|
| REQ-CAT-01 | Mở danh sách | 5 | 001, 004, 005, 020, 049 | ✅ Positive + Negative (chưa đăng nhập) |
| REQ-CAT-02 | 10 cột | 2 | 001, 046 | ✅ Positive (quan sát tĩnh) |
| REQ-CAT-03 | Phân trang 15 dòng | 2 | 009, 049 | ✅ Positive + biên (trang cuối) |
| REQ-CAT-04 | # liên tục | 1 | 010 | ✅ Positive (3 trang) |
| REQ-CAT-05 | Sắp Order Level giảm dần | 1 | 011 | ✅ Positive |
| REQ-CAT-06 | Cột Parent | 3 | 006, 012, 018 | ✅ Positive + Negative (con mồ côi) |
| REQ-CAT-07 | Cột Level | 2 | 006, 012 | ✅ Positive (0/1/2) |
| REQ-CAT-08 | Cột Banner/Icon | 1 | 013 | ✅ Positive (có/không ảnh) |
| REQ-CAT-09 | Cột Commission | 1 | 050 | ✅ Positive (định dạng) |
| REQ-CAT-10 | Màn hẹp, nút `+` | 1 | 046 | ✅ Positive |
| REQ-CAT-11 | Tìm kiếm | 6 | 014, 015, 042, 047, 048, 049 | ✅ Positive + Negative + EP |
| REQ-CAT-12 | Tìm không khớp | 1 | 015 | ✅ Negative |
| REQ-CAT-13 | Featured | 2 | 016, 046 | ✅ Positive (bật/tắt) |
| REQ-CAT-14 | Nút Edit | 3 | 005, 007, 020 | ✅ Positive |
| REQ-CAT-15 | Hộp xác nhận xoá | 4 | 008, 017, 020, 047 | ✅ Positive + Negative (Cancel, Esc) |
| REQ-CAT-16 | Kết quả xoá | 4 | 008, 018, 019, 040 | ✅ Positive + Negative (`@NeedsVerify`) |
| REQ-CAT-17 | Form tạo | 2 | 002, 047 | ✅ Positive |
| REQ-CAT-18 | Name bắt buộc | 2 | 021, 022 | ✅ Negative (trống, khoảng trắng) |
| REQ-CAT-19 | Parent Category | 3 | 002, 012, 027 | ✅ Positive + EP |
| REQ-CAT-20 | Ordering Number | 4 | 002, 006, 011, 028 | ✅ Positive + Negative + Boundary |
| REQ-CAT-21 | Type | 2 | 002, 029 | ✅ Positive (2 giá trị) |
| REQ-CAT-22 | Banner/Icon | 2 | 002, 030 | ✅ Positive + bỏ chọn |
| REQ-CAT-23 | Meta | 2 | 002, 031 | ✅ Positive + trống |
| REQ-CAT-24 | Filtering Attributes | 2 | 002, 032 | ✅ Positive (0/1/2 mục) |
| REQ-CAT-25 | Lưu thành công | 9 | 006, 022, 023, 024, 025, 041, 043, 044, 048 | ✅ Positive + Negative + Boundary (1/49/50/51/255) |
| REQ-CAT-26 | Tính Level | 3 | 006, 012, 038 | ✅ Positive (tạo + đổi cha) |
| REQ-CAT-27 | Sinh Slug | 3 | 025, 026, 033 | ✅ Positive + ký tự đặc biệt |
| REQ-CAT-28 | Tên trùng | 1 | 026 | ✅ Positive |
| REQ-CAT-29 | Form sửa | 2 | 003, 036 | ✅ Positive |
| REQ-CAT-30 | Dịch Name | 3 | 003, 036, 048 | ✅ Positive |
| REQ-CAT-31 | Chặn chọn cha sai | 1 | 037 | ✅ Negative (chính nó/con/cháu) + Positive |
| REQ-CAT-32 | Lưu sửa | 6 | 007, 034, 038, 040, 044, 045 | ✅ Positive + Negative (hết phiên) |
| REQ-CAT-33 | Sửa Slug | 2 | 034, 035 | ✅ Positive + Negative (trùng, `@KnownIssue`) |
| REQ-CAT-34 | Category ở Product vật lý | 4 | 019, 039, 040, 041 | ✅ Positive + Negative (Digital không có) |
| REQ-CAT-35 | Category ở Product số | 1 | 039 | ✅ Positive + Negative |

**Kiểm chiều ngược (6b):** không TC nào gánh ≥ 2 REQ mà các REQ đó không có TC khác. TC_013 ban đầu gánh REQ-08 + REQ-09 → đã tách REQ-09 sang TC_050.

## Bảng Đối Soát Evidence

| Ảnh / tài liệu | Đã mở | Tình trạng | TC dựa vào |
|---|---|---|---|
| `category_list_default_viewport.jpg` | ✅ | Đủ | 001, 046 |
| `category_list_row_expanded_viewport.jpg` | ✅ | Đủ | 046 |
| `category_list_featured_on_1920.jpg` | ✅ | Đủ | 001, 016 |
| `category_create_form_default_viewport.jpg` | ✅ | ⚠️ Cắt cụt — không thấy nút `Save` | 002 (vị trí `Save` lấy từ DOM) |
| `category_edit_en_default.jpg` | ✅ | ⚠️ Cắt cụt — không thấy nút `Save` | 003 |
| `category_parent_cycle_incident.md` | ✅ | Văn bản | 012, 037, 038, PC-4 |

### Vùng chưa có evidence — TC gắn `@NeedsVerify`

| TC | Vùng chưa quan sát | Đề xuất |
|---|---|---|
| 018, 019 | Xoá cha có con / xoá danh mục có sản phẩm (AMB-CAT-05). Đã quan sát ở run_1793261923: con được đưa về gốc; danh mục gắn sản phẩm bị xoá ngay, sản phẩm mất danh mục, không cảnh báo | **Chờ PO chốt AMB-CAT-05** rồi mới sửa Expected hoặc báo bug |

> Đã gỡ `@NeedsVerify` cho 008, 015, 030, 034, 044, 047 sau [run_1793261923](../../executions/category/web/run_1793261923/execution_report.md) — xem [báo cáo review 29-10-2026](review/testcase_review_report_web_20261029.md).

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC ID / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | TC_001–003 (3 TC · 3 ca, bảng kiểm 19 mục) |
| 1 | Open form / điều hướng | ✅ | TC_004–005 (2 TC · 5 ca) |
| 1 | Input valid · Save · Verify data | ✅ | TC_006–008 (3 TC · 3 ca: tạo, sửa, xoá) |
| 2 | UI Behavior | ✅ | TC_027 (tìm trong ô cha), TC_030 (chọn/bỏ ảnh), TC_036 (đổi tab ngôn ngữ), TC_046 (`+`/`−`) |
| 2 | Required | ✅ | TC_021–022 (2 TC · 4 ca) |
| 2 | Validation | ✅ | TC_021–025, 028, 033–035 (9 TC · 24 ca). Name (Text): Required · Whitespace-only · Min (1) · Max (50) · Max+1/255 · ký tự đặc biệt · số · Unicode · trim đầu cuối · XSS (TC_041) — đủ 8/8 mục · Ordering Number (Number): trống · chữ · âm · biên trên · vượt biên · số lẻ — đủ 6/6 · Slug (Text): sinh tự động · ký tự đặc biệt · chữ hoa · trống · trùng · Type/Parent (Dropdown): giá trị mặc định · đủ lựa chọn · tìm kiếm · bỏ chọn · Filtering Attributes (Multi-Select): 0/1/2 mục · Banner/Icon (File chọn từ thư viện): chọn · bỏ chọn — loại/dung lượng tệp ➖ vì tải tệp thuộc module thư viện tệp, không thuộc form này · Meta (Text/Textarea): trống · có giá trị · nhiều dòng |
| 2 | Equivalence Partitioning | ✅ | TC_014 (6 cách gõ từ khoá), TC_025 (4 lớp ký tự), TC_029 (2 Type), TC_039 (Type × form sản phẩm) |
| 2 | Boundary Value Analysis | ✅ | TC_023–024 (Name 1/49/50/51/255), TC_028 (2147483647 / 2147483648), TC_009 (trang cuối), TC_010 (biên số thứ tự) |
| 2 | Business Rule | ✅ | TC_011 (sắp xếp), TC_012 · 038 (Level), TC_026 (cho trùng tên), TC_033–036 (Slug, đa ngôn ngữ), TC_037 (chặn cha sai) |
| 2 | Decision Table | ✅ | TC_039 (Type Physical/Digital × form Product thường/số = 4 ô) |
| 2 | State Transition | ➖ | Danh mục không có trạng thái; Featured là cờ bật/tắt, đã phủ ở TC_016 |
| 2 | Dependency | ✅ | TC_019 (xoá danh mục có sản phẩm), TC_039–040 (Category → Product), TC_018 (cha–con) |
| 2 | Use Case / Scenario | ✅ | TC_040 (tạo → đổi tên → xoá → Product), TC_012 (dựng cây 3 cấp) |
| 2 | Save / Edit / Delete | ✅ | TC_006–008, TC_017 (huỷ xoá), TC_018–019 |
| 2 | Error Guessing | ✅ | TC_043 (bấm Save 2 lần), TC_045 (2 tab cùng sửa), TC_015c (tìm toàn khoảng trắng) |
| 3 | Permission | ✅ + ⏭️ | TC_020 (chưa đăng nhập + admin hiện tại). Role khác: ⏭️ chưa có tài khoản (AMB-CAT-08). **Quyết định: user, 29-10-2026 (Q8).** Rà lại khi được cấp tài khoản role khác |
| 3 | Security | ✅ | TC_041 (XSS/SQL ở Name, 3 ca), TC_042 (XSS/SQL ở tìm kiếm, 3 ca), TC_020, TC_044 |
| 3 | Security — tự gửi dữ liệu tạo vòng lặp cha–con | ⏭️ Cố ý bỏ | Gây treo máy chủ ≈ 3 phút và không dọn được qua giao diện (sự cố 28→29-10-2026). **Quyết định: user, 29-10-2026 (Q3).** Dev kiểm trên môi trường riêng. Rà lại khi máy chủ có kiểm tra vòng lặp |
| 3 | HTTP Status | ✅ | TC_022 (`@TechCheck` — Name toàn khoảng trắng trả 500) |
| 3 | Session / Concurrency | ✅ | TC_044 (hết phiên), TC_045 (sửa đồng thời), TC_043 (double submit) |
| 3 | API | ⏭️ Cố ý bỏ | QA không có quyền gọi API — đội Dev xác minh (README requirements, chốt 27-10-2026). Rà lại khi QA được cấp quyền |
| 3 | Database | ⏭️ Cố ý bỏ | Không có quyền CSDL — đội Dev xác minh. Rà lại khi được cấp quyền |
| 3 | Integration | ⏭️ Cố ý bỏ | QA không có quyền kiểm tầng tích hợp — đội Dev xác minh. Phần nhìn thấy trên UI đã phủ ở TC_039–040 |
| 3 | Logging / Audit | ⏭️ Cố ý bỏ | QA chưa có quyền xem nhật ký — đội Dev xác minh. Rà lại khi được cấp quyền |
| 4 | Compatibility | ⏭️ Cố ý bỏ | Chỉ kiểm Chrome. **Quyết định: QA, 29-10-2026.** Rà lại khi hệ thống cam kết hỗ trợ trình duyệt khác |
| 4 | Responsive / UI Stability | ✅ | TC_046 (1 TC · 4 ca: 1920 · 1366 · mở/thu hàng chi tiết) |
| 4 | Accessibility | ✅ | TC_047 (Enter, Tab, Esc) |
| 4 | Localization / UTF-8 | ✅ | TC_036, TC_048 (tiếng Việt, emoji, chữ Nhật) |
| 4 | Performance | ✅ | TC_049 (quan sát ≤ 3 giây, `@AssumptionBased` — chưa có SLA) |
| 4 | Regression | ➖ | Module mới, chưa có bug nào được đóng (`docs/bugs/category/` chưa có) |
| 4 | E2E | ✅ | TC_040 (Category → Product), chung với nhánh Dependency |

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–040, 050 |
| Performance Efficiency | ✅ Có TC | TC_049 (quan sát bằng mắt). Đo tải bằng công cụ: ➖ đội Dev / QA lead khi có SLA |
| Compatibility | ✅ Có TC | TC_039–040 (cùng tồn tại với module Product). Đa trình duyệt: chỉ Chrome — QA quyết định |
| Interaction Capability | ✅ Có TC | TC_021–022 (chặn thao tác sai), TC_017 (huỷ xoá), TC_047 (bàn phím), TC_015 (trạng thái rỗng) |
| Reliability | ✅ Có TC | TC_043, TC_045, TC_044, TC_022 (không được lỗi 500) |
| Security | ✅ Có TC | TC_020, TC_041, TC_042, TC_044. Pentest và gửi dữ liệu trực tiếp: ➖ đội Dev / security |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn — code review của đội Dev |
| Flexibility | ✅ Có TC | TC_046 (2 kích thước màn hình), TC_036 · 048 (đa ngôn ngữ) |
| Safety | ✅ Có TC | TC_008, TC_017 (xác nhận trước khi xoá), TC_018–019 (không để dữ liệu mồ côi), TC_037 (chặn vòng lặp trên form) |

## Đối soát cột Automation

| Nền tảng | Yes | Partial | No | ⏸️ Hoãn |
|---|---|---|---|---|
| Web | 47 | 3 | 0 | 8 (trong số `Yes`/`Partial`) |

### Điều kiện cần chuẩn bị

| # | Điều kiện | Ai cấp | Trạng thái | TC phụ thuộc |
|---|---|---|---|---|
| 1 | Được tạo / sửa / xoá danh mục test `AutoCat*_<ts>`, dọn ngay sau test | User | ✅ Đã có (môi trường không dùng chung, 28-10-2026) | Mọi TC có PC-2 / PC-3 |
| 2 | Được tạo sản phẩm test để kiểm xoá danh mục có sản phẩm | User | ⏳ Chưa hỏi riêng — môi trường không dùng chung nên dự kiến được | TC_019 |
| 3 | Chốt câu chữ thông báo xoá, hành vi xoá cha có con (AMB-CAT-05) | PO / Dev | ⏳ Chưa có | Nhóm ⏸️ Hoãn |
| 4 | Dọn 4541–4543 (AMB-CAT-09) để danh sách không còn dòng Level âm | Quản trị máy chủ | ⏳ Chưa có | TC_011a, 010c (dữ liệu lạ có thể làm sai số đếm) |

### TC Partial · No · Hoãn

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_CAT_TC_019 | Web | Partial | 2 · dữ liệu chéo module | Cần tạo sản phẩm đủ trường bắt buộc — form Product dài, nên chạy tay tới khi có Page Object Product |
| AEC_CAT_TC_046 | Web | Partial | 2 · Expected cần mắt người | Tự động: số cột hiện, nút `+`. Kiểm tay: không có cuộn ngang, chữ không đè nhau |
| AEC_CAT_TC_049 | Web | Partial | 2 · không có SLA | Ngưỡng 3 giây là giả định; kết quả phụ thuộc mạng |
| AEC_CAT_TC_018 · 019 | Web | ⏸️ Hoãn | — | `@NeedsVerify` — chờ PO chốt AMB-CAT-05 rồi mới automate |

**Lưu ý automation:** Delete là liên kết GET tới `/admin/categories/destroy/<id>` — automation **chỉ** xoá qua hộp xác nhận, không mở thẳng đường dẫn. Không bao giờ dựng quan hệ cha–con ngoài ô `Parent Category` (PC-4).

**Thứ tự ưu tiên automate:** `@Smoke`/`@CriticalPath` (TC_001–008, 012, 016, 037) → TC nhiều biến thể Validation/BVA (TC_014, 023–025, 028) → phần còn lại.

### Automation đã có (03-11-2026)

Selenium Java · `src/test/java/com/aec/tests/` — `CategoryTest` · `CategoryFormTest` · `CategoryListTest` (nền `CategoryTestBase`). Label `testId` = TC ID. Mọi danh mục test mang mã `TC<nnn><biến thể>_<epoch>_<hex>`, tên ≤ 50 ký tự; dọn sau mỗi test, danh mục có Level lớn nhất xoá trước (cháu → con → cha). Không đụng 4541–4543 (PC-4).

| Phạm vi | TC |
|---|---|
| Đã automate | 001–018, 020, 021, 023 (`b`, `c`), 024–050 — **48 TC** |
| TC_018 (đang ⏸️ Hoãn) | Automate phần không phụ thuộc AMB-CAT-05: chấp nhận cả "chặn xoá" lẫn "xoá kèm con", chỉ đỏ khi con còn lại mà cha đã mất |
| Không automate | **019** (phải tạo sản phẩm thật — Partial) · **022** (Name toàn khoảng trắng — nếu lọt thì tạo danh mục không truy vết được để dọn) · **023-a** (Name 1 ký tự `Q`, cùng lý do) |
| Lệch nhỏ so với TC | TC_011-b: kiểm danh mục test nằm trang 1, mọi dòng phía trên có Order Level ≥ 999999999 (dữ liệu dùng chung đang có dòng 2147483647 nên không thể đứng dòng 1) · TC_039, 040, 041-bước 4: đọc danh sách lựa chọn của ô Category thay vì gõ vào ô tìm · TC_027, 037: gõ tên đầy đủ có mã test thay vì tiền tố `AutoCatP_TC027` (tiền tố cũ trùng dữ liệu các lần chạy trước) · TC_049: thời gian đo từ trình duyệt tự động, chỉ tham khảo |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 6 biến thể (TC_014, 028); TC thiết kế để FAIL (022, 024, 035) tách riêng khỏi TC PASS |
| 1 | Unique TC ID | ✅ 50 ID, không trùng, đúng mẫu `AEC_CAT_TC_<3 số>` |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected đánh số theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Mọi dữ liệu có giá trị cụ thể, `<ts>` có ví dụ `1793200000` |
| 4 | Field validation coverage | ✅ Đối soát từng trường — chi tiết ở dòng `V2 · Validation` |
| 5 | Automation metadata | ✅ 50/50 có `Automation` · `Auto Type` · tag; ngoại lệ ghi ở mục Đối soát cột Automation |
| 6 | Requirement coverage | ✅ 35/35 REQ; 6b đã xử lý (tách TC_050) |
| 7 | Evidence-verified | ✅ Đã mở 5/5 ảnh + 1 tài liệu sự cố; 2 ảnh cắt cụt đã ghi chú; vùng thiếu gắn `@NeedsVerify` (8 TC) |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không có thuật ngữ DOM ở phần TC chính; phần kỹ thuật nằm dưới `🔧` và TC mang `@TechCheck` (022) |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm, ô `➖` có người chịu trách nhiệm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴; `UI cơ bản` · `Validation` · `Permission` đều ✅ |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 29-10-2026 | `/generate-testcases-manual-rbt category` (FULL RBT) | AEC_CAT_TC_001 → 050 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-CAT-01 → 35, độ hạt GỘP, giả định Q1–Q10. TC_050 tách từ TC_013 theo Quality Gate 6b |
| 29-10-2026 | `/review-testcases category` mode FIX — [báo cáo](review/testcase_review_report_web_20261029.md), căn cứ run_1793261923 | 003, 008, 015, 028, 030, 033, 034, 042, 044, 047 | 🟡 Sửa | 003 mục `4` và 033a: Slug bỏ `_` theo REQ-CAT-27 · 028f: trình duyệt chặn `1.5` · 008, 044: Expected ghi câu chữ đã quan sát · 034b cụ thể hoá · 042: thêm hậu tố `zzq<ts>` vào Test Data · gỡ `@NeedsVerify` 008, 015, 030, 034, 044, 047. Giữ nguyên 018, 019 (chờ AMB-CAT-05). Không có git — bản trước khi sửa lưu ở scratchpad phiên làm việc, không có mốc git |
| 03-11-2026 | `/generate-automation-web` mode TC | 48 TC (xem mục Automation đã có) | 🟢 Thêm | Sinh automation Selenium Java. Không đổi nội dung TC nào |
