# Test Cases — Manage Brand (tổng 50 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_BRAND_SUMMARY.md](../../requirements/brand/REQUIREMENTS_BRAND_SUMMARY.md) · [web/requirements_brand_web.md](../../requirements/brand/web/requirements_brand_web.md) |
| Dải TC ID đã dùng | `AEC_BRAND_TC_001` → `AEC_BRAND_TC_050` — chung mọi nền tảng. TC kế tiếp: `AEC_BRAND_TC_051` |
| Mode · độ hạt | FULL RBT · **GỘP** — 50 TC · 99 ca kiểm (TC_034 bỏ biến thể `c` ngày 27-10-2026; biến thể `a`, `b`… tính riêng; TC không có biến thể tính 1) |
| Mức rủi ro · độ sâu | Module `Cao` → **Đầy đủ**. Căn cứ: Xóa không hoàn tác được, xóa qua liên kết GET (RISK-BRAND-01), brand là nguồn dữ liệu của Product.<br>**Hạ xuống Tiêu chuẩn khi:** Delete đổi sang xóa mềm có khôi phục **và** PO kết luận AMB-BRAND-01 không phải lỗi |
| Phạm vi REQ | `REQ-BRAND-01` → `REQ-BRAND-28` (28/28). Không REQ nào bị loại khỏi phạm vi |
| Giả định đang áp | Q1–Q10 (= AMB-BRAND-01, 02 → 10, 12) — user chấp nhận toàn bộ assumption ngày 27-10-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Ngày sinh | 27-10-2026 |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/test_cases_brand_web.md](web/test_cases_brand_web.md) | V1 Smoke · V2a Danh sách & tìm kiếm · V2b Validation · V2c Nghiệp vụ/CRUD/phụ thuộc · V3 Technical · V4 Non-functional | 50 | 001–050 | REQ-BRAND-01 → 28 |

## Bảng Đối Soát Coverage (toàn module)

| REQ ID | Mô tả ngắn | Web — số TC | TC IDs | Đủ Positive/Negative/Boundary? |
|---|---|---|---|---|
| REQ-BRAND-01 | Mở danh sách từ menu | 4 | 001, 003, 046, 047 | ✅ Positive + Negative (chưa đăng nhập) |
| REQ-BRAND-02 | Bảng 4 cột | 2 | 001, 049 | ✅ Positive (quan sát tĩnh) |
| REQ-BRAND-03 | 15 brand/trang | 1 | 010 | ✅ Positive |
| REQ-BRAND-04 | # liên tục qua trang | 1 | 011 | ✅ Positive (3 trang) |
| REQ-BRAND-05 | Sắp xếp Name tăng dần | 1 | 012 | ✅ Positive (hoa/thường trộn) |
| REQ-BRAND-06 | Ảnh giữ chỗ khi chưa có logo | 1 | 005 | ✅ Positive |
| REQ-BRAND-07 | Nút Edit mỗi dòng | 2 | 004, 047 | ✅ Positive |
| REQ-BRAND-08 | Phân trang | 2 | 013, 014 | ✅ Positive + biên (trang cuối, `@KnownIssue`) |
| REQ-BRAND-09 | Tìm bằng Enter, giữ từ khóa | 3 | 016, 020, 050 | ✅ Positive + Negative (dữ liệu bất thường) |
| REQ-BRAND-10 | Khớp chuỗi con | 1 | 017 | ✅ Positive (đầu/giữa/cuối) |
| REQ-BRAND-11 | Không phân biệt hoa thường | 1 | 018 | ✅ Positive |
| REQ-BRAND-12 | ≤ 15 kết quả không phân trang | 1 | 016 | ✅ Positive — REQ duy nhất chỉ có TC_016 (phép thử 6b đạt) |
| REQ-BRAND-13 | Không có kết quả | 1 | 019 | ✅ Negative |
| REQ-BRAND-14 | Delete mở hộp xác nhận | 2 | 009, 047 | ✅ Positive |
| REQ-BRAND-15 | Cancel không xóa | 1 | 041 | ✅ Negative (3 cách đóng — ESC `@KnownIssue`) |
| REQ-BRAND-16 | Xác nhận xóa | 2 | 009, 048 | ✅ Positive + Negative (bảo mật) |
| REQ-BRAND-17 | Form Add New Brand | 7 | 001, 006, 030, 031, 032, 049, 050 | ✅ Positive + Negative + Boundary (Meta Title 255/256) |
| REQ-BRAND-18 | Name bắt buộc khi tạo | 2 | 021, 023 | ✅ Negative (trống, toàn khoảng trắng) |
| REQ-BRAND-19 | Thư viện Logo | 4 | 007, 033, 035, 036 | ✅ Positive + Negative (sai loại tệp) |
| REQ-BRAND-20 | Đóng thư viện không chọn | 1 | 034 | ✅ Negative (2 cách đóng: `×`, ESC — ESC `@KnownIssue`) |
| REQ-BRAND-21 | Lưu tạo brand | 9 | 006, 007, 024, 025, 026, 027, 028, 029, 044 | ✅ Positive + Negative + Boundary (Name 1/254/255/256/1000) |
| REQ-BRAND-22 | 2 tab ngôn ngữ | 3 | 002, 004, 037 | ✅ Positive |
| REQ-BRAND-23 | Lưu sửa theo ngôn ngữ | 3 | 008, 038, 045 | ✅ Positive + đoán lỗi (2 tab) |
| REQ-BRAND-24 | Chỉ Name Translatable | 2 | 002, 037 | ✅ Positive |
| REQ-BRAND-25 | Slug sửa được | 3 | 002, 039, 040 | ✅ Positive + Negative (Slug trùng) |
| REQ-BRAND-26 | Name bắt buộc khi sửa | 1 | 022 | ✅ Negative (2 tab) |
| REQ-BRAND-27 | Id không tồn tại | 1 | 042 | ✅ Negative |
| REQ-BRAND-28 | Nguồn dropdown Product | 2 | 015, 043 | ✅ Positive + `@KnownIssue` |

**28/28 REQ có ≥ 1 TC.** Phép thử 6b (TC gánh ≥ 2 REQ mà các REQ đó không có TC khác chống lưng): **không vi phạm**.

## Bảng Đối Soát Evidence

| Ảnh evidence | Màn hình / trạng thái | TC dựa vào | Đầy đủ? |
|---|---|---|---|
| `brand_list_default_viewport.png` | All Brands mặc định + form Add New Brand | 001, 003, 005, 006, 010, 049 | 🟡 Thiếu tiêu đề `All Brands` và vùng phân trang — đã chốt bằng đọc DOM lúc recon |
| `brand_list_search_lowercase_viewport.png` | Kết quả tìm `autobrandedit` | 016, 017, 018 | ✅ |
| `brand_delete_confirm_modal_viewport.png` | Hộp `Delete Confirmation` | 009, 041 | ✅ (ảnh nhỏ 372×210, đọc được nhãn) |
| `brand_logo_uploader_select_file_viewport.png` | Thư viện tệp — tab Select File | 007, 033, 034, 035 | ✅ |
| `brand_edit_en_default_viewport.png` | Brand Information — tab English | 002, 004, 008, 022, 039 | ✅ |
| [run_1793105365](../../executions/brand/web/run_1793105365/execution_report.md) | Thông báo sau tạo/sửa/xóa thành công: `Brand has been inserted / updated / deleted successfully` | 006–009, 024, 026, 037–040, 044, 050 | ✅ Quan sát khi chạy 27-10-2026 (chưa có ảnh riêng) |
| (không có) | Tab Vietnamese · trang `Page Not Found!` · dropdown Brand ở Product | 037, 038, 042, 043, 015 | 🟡 Có số liệu đọc DOM lúc recon, chưa có ảnh |
| [run_1793105365](../../executions/brand/web/run_1793105365/execution_report.md) | Tìm không có kết quả · màn 768 px (`evidence/AEC_BRAND_TC_049_c_768x1024.jpg`) · ESC/bấm ngoài hộp · thứ tự Tab | 019, 034, 041, 049, 050 | ✅ Quan sát khi chạy 27-10-2026 (TC_019: dòng `Nothing found` — ảnh [run_1793109740](../../executions/brand/web/run_1793109740/evidence/AEC_BRAND_TC_008_step6_nothing_found.jpg), đính chính 28-10-2026) |
| (không có) | Thông báo lỗi validation phía máy chủ (máy chủ đang trả lỗi 500 / lưu cắt im lặng) · upload sai loại · Slug trùng | 023, 025, 031, 036, 040 | 🔴 THIẾU — TC gắn `@NeedsVerify` (AMB-BRAND-04, 05, 06) |

### Vùng chưa có evidence — đề xuất recon bổ sung

1. Message lỗi máy chủ khi Name trống/toàn khoảng trắng, Name hoặc Meta Title quá dài, Slug trùng — chờ Dev sửa lỗi 500 / cắt im lặng và PO chốt (AMB-BRAND-04, 05, 06)
2. Ảnh tab Vietnamese, trang `Page Not Found!`, dropdown Brand ở Add New Product, vùng phân trang (đã quan sát khi chạy run_1793105365, chưa chụp ảnh riêng)
3. Upload tệp sai loại (TC_036) và đường dẫn xóa trực tiếp (TC_048) — cần môi trường riêng

> Đã giải quyết bởi run_1793105365: câu thông báo thành công (AMB-BRAND-07), trạng thái tìm không kết quả (AMB-BRAND-08), chọn 1 ảnh Logo (AMB-BRAND-12), ESC / bấm ra ngoài hộp, thứ tự Tab, màn 768 px.

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC ID / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | TC_001–002 (2 TC · 2 ca, bảng kiểm 11 mục) |
| 1 | Open form | ✅ | TC_003–004 (2 TC · 4 ca) |
| 1 | Display | ✅ | TC_005 (1 TC · 1 ca) |
| 1 | Input valid · Save · Verify data | ✅ | TC_006–009 (4 TC · 4 ca: tối thiểu, đầy đủ, sửa, xóa) |
| 2 | UI Behavior | ✅ | TC_035 (chọn/bỏ chọn ảnh), TC_037 (đổi tab ngôn ngữ) |
| 2 | Required | ✅ | TC_021–023 (3 TC · 6 ca) |
| 2 | Validation | ✅ | TC_024–036 (13 TC · 30 ca). Name (Text): đủ 8/8 mục bảng Text · Meta Title (Text): 5/8 — bỏ Required (trường không bắt buộc), Whitespace-only và Leading/trailing (không có quy tắc riêng, chung cơ chế với Name) · Meta description (Textarea): 4/5 — Resize và Character counter không có trên UI (1 mục ghi lý do) · Logo (File Upload): 4/6 — Max size và tên tệp đặc biệt `⏭️` (upload lên thư viện dùng chung; rà lại khi có môi trường riêng) |
| 2 | Equivalence Partitioning | ✅ | TC_017 (đầu/giữa/cuối), TC_026 (4 lớp ký tự), TC_036 (3 loại tệp) |
| 2 | Boundary Value Analysis | ✅ | TC_024–025 (Name 1/254/255/256/1000), TC_030a–031 (Meta Title 255/256), TC_011 (biên số thứ tự), TC_014 (trang cuối) |
| 2 | Business Rule | ✅ | TC_012 (sắp xếp), TC_029 (cho trùng tên), TC_037–040 (đa ngôn ngữ, Slug) |
| 2 | Decision Table | ➖ | Không có quy tắc nào kết hợp ≥ 3 điều kiện ra 1 kết quả |
| 2 | State Transition | ➖ | Brand không có trạng thái (requirements mục Ma trận Trạng thái: Không áp dụng) |
| 2 | Dependency | ✅ | TC_043 (1 TC · 3 ca: tạo/đổi tên/xóa → dropdown Product), TC_015 (`@KnownIssue`) |
| 2 | Dependency — xóa brand đang gắn sản phẩm | ⏭️ Cố ý bỏ | Không tạo sản phẩm trên môi trường dùng chung (Q10). **Quyết định: user, 27-10-2026.** Rà lại khi có môi trường riêng hoặc được phép tạo sản phẩm test |
| 2 | Use Case / Scenario | ✅ | TC_043 (chuỗi tạo → đổi tên → xóa), TC_006–009 |
| 2 | Save / Edit / Delete | ✅ | TC_006–009, TC_041 (3 ca hủy), TC_042 (2 ca) |
| 2 | Error Guessing | ✅ | TC_020 (6 ca dữ liệu lạ), TC_044 (bấm Save 2 lần), TC_045 (2 tab cùng sửa) |
| 3 | Permission | ✅ + ⏭️ | TC_047 (role hiện tại). Role khác: ⏭️ chưa có tài khoản (AMB-BRAND-02). **Quyết định: user, 27-10-2026.** Rà lại khi được cấp tài khoản role khác |
| 3 | Security | ✅ | TC_046 (3 ca chưa đăng nhập / sau đăng xuất), TC_027 · TC_032 · TC_020 (XSS/SQL), TC_048 (xóa không qua xác nhận, `@TechCheck`) |
| 3 | API | ➖ | QA không có quyền gọi API — đội Dev xác minh (README requirements, chốt 27-10-2026) |
| 3 | Database | ➖ | Không có quyền truy cập CSDL — đội Dev xác minh |
| 3 | Integration | ➖ | Module không tích hợp bên thứ ba; QA không có quyền kiểm tầng tích hợp — đội Dev xác minh |
| 3 | Logging / Audit | ➖ | Requirements không có yêu cầu nhật ký; QA không có quyền xem nhật ký — đội Dev xác minh |
| 4 | Compatibility | ⏭️ Cố ý bỏ | Chỉ kiểm Chrome. **Quyết định: QA, 27-10-2026.** Rà lại khi hệ thống cam kết hỗ trợ trình duyệt khác |
| 4 | Responsive / UI Stability | ✅ | TC_049 (1 TC · 3 ca: 1920 · 1366 · 768) |
| 4 | Accessibility | ✅ | TC_050 (Tab, Enter, viền focus) |
| 4 | Performance | ⏭️ Cố ý bỏ | Danh sách đã phân trang, không có ngưỡng thời gian cam kết. **Quyết định: QA, 27-10-2026.** Rà lại khi có SLA thời gian tải |
| 4 | Regression | ➖ | Module mới, chưa có bug nào được đóng (`docs/bugs/brand/` chưa có) |
| 4 | E2E | ✅ | TC_043 (Brand → Product), chung với nhánh Dependency |

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–045 |
| Performance Efficiency | ➖ Ngoài phạm vi | Không có công cụ tải và ngưỡng cam kết — QA lead quyết định, rà lại khi có SLA |
| Compatibility | ✅ Có TC | TC_043, TC_015 (tương tác với module Product). Đa trình duyệt: chỉ Chrome — QA quyết định |
| Interaction Capability | ✅ Có TC | TC_021–023 (chặn thao tác sai), TC_041 (hủy xóa), TC_050 (bàn phím), TC_019 (trạng thái rỗng) |
| Reliability | ✅ Có TC | TC_044 (bấm 2 lần), TC_045 (sửa đồng thời), TC_020 (dữ liệu bất thường) |
| Security | ✅ Có TC | TC_046, TC_047, TC_048, TC_027, TC_032. Pentest / quét lỗ hổng: ➖ đội Dev / security |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn — code review của đội Dev |
| Flexibility | ✅ Có TC | TC_049 (3 kích thước màn hình), TC_037–038 (đổi ngôn ngữ nội dung) |
| Safety | ✅ Có TC | TC_009, TC_041 (xác nhận trước khi xóa không hoàn tác) |

## Đối soát cột Automation

| Nền tảng | Yes | Partial | No | ⏸️ Hoãn |
|---|---|---|---|---|
| Web | 46 | 3 | 1 | 4 (trong số `Yes`) |

### Điều kiện cần chuẩn bị

| # | Điều kiện | Ai cấp | Trạng thái | TC phụ thuộc |
|---|---|---|---|---|
| 1 | Được phép tạo/xóa brand test `Auto*_<ts>` trên môi trường dùng chung, dọn ngay sau test | User | ✅ Đã có (Q10, 27-10-2026) | Mọi TC có PC-2 / PC-3 |
| 2 | Được phép tải tệp test lên thư viện tệp dùng chung, hoặc có môi trường riêng | User / Dev | ⏳ Chưa có | TC_036 |
| 3 | Chốt câu chữ thông báo thành công / lỗi máy chủ (AMB-BRAND-05, 07) | PO / Dev | ⏳ Chưa có | Nhóm ⏸️ Hoãn |

### TC Partial · No · Hoãn

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_BRAND_TC_015 | Web | Partial | 2 · chạy lại không cùng kết quả | Số brand thay đổi liên tục do người khác tạo/xóa — so số ở hai màn có thể lệch theo thời điểm; kiểm tay khi cần kết luận AMB-BRAND-01 |
| AEC_BRAND_TC_036 | Web | Partial | 2 · môi trường | Điều kiện #2 |
| AEC_BRAND_TC_049 | Web | Partial | 2 · Expected cần mắt người | Tự động: không có cuộn ngang, đủ ô và nút. Kiểm tay: chữ không đè nhau |
| AEC_BRAND_TC_048 | Web | No | 3 · chính sách | RISK-BRAND-01: automation **không** được mở đường dẫn xóa trực tiếp — chạy tay trên brand test riêng |
| AEC_BRAND_TC_023 · 025 · 031 · 040 | Web | Yes · ⏸️ Hoãn | — | `@NeedsVerify` — chờ chốt AMB-BRAND-04 · 05 · 06 (15 TC khác đã hết Hoãn sau run_1793105365, 27-10-2026) |

**Thứ tự ưu tiên automate:** `@Smoke`/`@CriticalPath` (TC_001–009, 041, 043, 046) → TC nhiều biến thể Validation/BVA (TC_020, 024–027, 030, 032) → phần còn lại.

### Automation đã có (03-11-2026)

Selenium Java · `src/test/java/com/aec/tests/` — `BrandTest` · `BrandEditTest` · `BrandValidationTest` · `BrandListTest`. Label `testId` = TC ID.

| Phạm vi | TC |
|---|---|
| Đã automate | 001–022, 024 (`b`, `c`), 025–035, 037–047, 049, 050 — **47 TC** |
| TC_025 · 031 · 040 (đang ⏸️ Hoãn) | Automate phần không phụ thuộc câu chữ AMB-BRAND-04 · 05 · 06: không tạo brand, không trang lỗi máy chủ, Slug B ≠ A. Chưa so câu thông báo |
| Không automate | **023** (Name toàn khoảng trắng — nếu lọt thì tạo brand không truy vết được để dọn) · **024-a** (Name 1 ký tự `Q`, cùng lý do) · **036** (điều kiện #2) · **048** (`No`) |
| Lệch nhỏ so với TC | TC_015, 043, 027-bước 5: đọc danh sách lựa chọn của dropdown Brand thay vì gõ vào ô tìm của dropdown · TC_024: tìm brand theo mã test thay vì 20 ký tự đầu · TC_013-d: trang 1 không mang `?page=1` trong URL nên chỉ kiểm số trang được tô và dữ liệu |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 6 biến thể (TC_020); không vi phạm bảng CẤM gộp (TC thiết kế để FAIL — 014, 015, 048 — đều tách riêng) |
| 1 | Unique TC ID | ✅ 50 ID, không trùng, đúng mẫu `AEC_BRAND_TC_<3 số>` |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected đánh số theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Mọi dữ liệu có giá trị cụ thể, `<ts>` có ví dụ `1793100000` |
| 4 | Field validation coverage | ✅ Đối soát theo bảng — chi tiết ở dòng `V2 · Validation` |
| 5 | Automation metadata | ✅ 50/50 có `Automation` · `Auto Type` · tag; ngoại lệ ghi ở mục Đối soát cột Automation |
| 6 | Requirement coverage | ✅ 28/28 REQ; 6b không vi phạm |
| 7 | Evidence-verified | ✅ Đã mở 5/5 ảnh; vùng thiếu đã gắn `@NeedsVerify` (22 TC lúc sinh → còn 6 TC sau run_1793105365) và liệt kê ở trên |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không còn thuật ngữ DOM ở phần TC chính; phần kỹ thuật nằm dưới `🔧` và TC mang `@TechCheck` (005, 021, 048) |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm, ô `➖` có người chịu trách nhiệm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴; 3 nhánh cấm `➖` đều ✅ |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 27-10-2026 | `/generate-testcases-manual-rbt brand` (FULL RBT) | AEC_BRAND_TC_001 → 050 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-BRAND-01 → 28, độ hạt GỘP, giả định Q1–Q10 |
| 27-10-2026 | `/review-testcases brand` mode FIX — [báo cáo](review/testcase_review_report_web_20261027.md) · theo [run_1793105365](../../executions/brand/web/run_1793105365/execution_report.md) | TC_008 · 034 · 041 · 006 · 007 · 009 · 013 · 019 · 028 · 035 · 038 · 039 · 042 · 045 · 049 · 050 | 🟡 Sửa | TC_008: tên mới không chứa tên cũ. TC_034: bỏ biến thể `c` (hộp cố ý không đóng khi bấm ngoài), `b` → `@KnownIssue`. TC_041-c → `@KnownIssue`. Gỡ `@NeedsVerify` 14 TC, thay ghi chú `⚠️` bằng điều quan sát được (`@NeedsVerify` 22 → 6; ca kiểm 100 → 99; ⏸️ Hoãn 19 → 4). **Không có mốc git** (thư mục không phải git repository) — bản trước khi sửa lưu ngoài dự án ở scratchpad của phiên `backup_before_review_fix_20261027/` |
| 28-10-2026 | Đính chính sau [run_1793109740](../../executions/brand/web/run_1793109740/execution_report.md) | TC_019 | 🟡 Sửa | Expected bước 3 sửa thành bảng hiện dòng `Nothing found` — nội dung sửa ngày 27-10-2026 dựa trên HTML máy chủ khi pane ẩn nên sai. Không có mốc git; chỉ đổi 1 ô Expected |
| 03-11-2026 | `/generate-automation-web` mode TC | 47 TC (xem mục Automation đã có) | 🟢 Thêm | Sinh automation Selenium Java. Không đổi nội dung TC nào |
