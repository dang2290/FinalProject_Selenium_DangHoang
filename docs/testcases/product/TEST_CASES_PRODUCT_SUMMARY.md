# Test Cases — Manage Product (tổng 77 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_PRODUCT_SUMMARY.md](../../requirements/product/REQUIREMENTS_PRODUCT_SUMMARY.md) · web: [requirements_product_web.md](../../requirements/product/web/requirements_product_web.md) |
| Mode · độ hạt | FULL RBT · **GỘP** — 77 TC · 103 ca kiểm (15 TC có bảng biến thể, 41 biến thể) |
| Dải TC ID đã dùng | `AEC_PROD_TC_001` → `AEC_PROD_TC_077` — mã kế tiếp `AEC_PROD_TC_078` |
| Phạm vi REQ | `REQ-PROD-01` → `REQ-PROD-60` (60/60). REQ-PROD-38, 59: chỉ phủ phần hiện diện, phần lưu ⏭️ Q14 |
| Mức rủi ro · độ sâu | `Cao` → **Đầy đủ**. Vượt khoảng 40–60 TC và ước tính 58 ở Bước 4 vì form 17 khối phải tách TC theo từng trường. **Nâng thêm khi:** có tài khoản role khác · có tệp mẫu Bulk Import · có đợt Flash Deal test |
| Giả định đang áp | Q1–Q14 (≈ AMB-PROD-01 → 18) — user chấp nhận toàn bộ 02-11-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Môi trường | **Dùng chung** (user xác nhận 03-11-2026) — chỉ đụng dữ liệu do TC tạo (PC-4). TC_057 chỉ tick dòng do TC tạo |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/parts/part_01_web_danh_sach.md](web/parts/part_01_web_danh_sach.md) | V1 · Danh sách · Công tắc (chứa PC, quy ước) | 21 | 001–021 | REQ-PROD-01 → 16, 27 → 29, 44, 45, 49, 51 → 53 |
| Web | [web/parts/part_02_web_tao_gia_bien_the.md](web/parts/part_02_web_tao_gia_bien_the.md) | Tạo · Giá · Biến thể | 32 | 022–052, 075 | REQ-PROD-17 → 26, 30 → 43 |
| Web | [web/parts/part_03_web_sua_digital_bulk.md](web/parts/part_03_web_sua_digital_bulk.md) | Sửa · Duplicate · Digital · Bulk · Luồng (gồm luồng liên module Brand → Category → Product → cửa hàng) · V3 · V4 | 24 | 053–074, 076–077 | REQ-PROD-25, 46 → 50, 54 → 60 và các REQ nhắc lại |

## Bảng Đối Soát Coverage

| REQ ID | Số TC | TC IDs | Ghi chú |
|---|---|---|---|
| REQ-PROD-01 | 4 | 001, 006, 068, 073 | ✅ |
| REQ-PROD-02 | 2 | 001, 016 | ✅ |
| REQ-PROD-03 | 1 | 001 | ✅ |
| REQ-PROD-04 | 1 | 012 | ✅ |
| REQ-PROD-05 | 1 | 014 | ✅ `@AssumptionBased` |
| REQ-PROD-06 | 1 | 006 | ✅ |
| REQ-PROD-07 | 1 | 013 | ✅ `@AssumptionBased` |
| REQ-PROD-08 | 2 | 015, 070 | ✅ Positive + Negative |
| REQ-PROD-09 | 1 | 016 | 🟡 Chỉ người bán không có sản phẩm |
| REQ-PROD-10 | 1 | 017 | ✅ `@KnownIssue` |
| REQ-PROD-11 | 5 | 001, 018, 074, 076, 077 | ✅ — 077 đối chiếu chi tiết Admin ↔ cửa hàng |
| REQ-PROD-12 → 14 | 1 mỗi REQ | 019 · 020 · 021 | ✅ |
| REQ-PROD-15 | 2 | 002, 073 | ✅ |
| REQ-PROD-16 | 1 | 002 | ✅ |
| REQ-PROD-17 | 3 | 022, 023, 072 | ✅ Negative |
| REQ-PROD-18 | 1 | 024 | ✅ Negative |
| REQ-PROD-19 | 2 | 025, 026 | ✅ Boundary 255 · 256 — `@KnownIssue` ở 025 (lưu thật 200 ký tự) |
| REQ-PROD-20 | 2 | 027, 028 | ✅ `@KnownIssue` ở 028 |
| REQ-PROD-21 | 2 | 029, 030 | ✅ `@KnownIssue` ở 029 |
| REQ-PROD-22 | 3 | 031, 076, 077 | ✅ — 076, 077 dùng Brand / Category vừa tạo |
| REQ-PROD-23 | 1 | 032 | ✅ Boundary |
| REQ-PROD-24 | 2 | 033, 071 | ✅ `@KnownIssue` ở 071 |
| REQ-PROD-25 | 1 | 069 | ✅ Negative |
| REQ-PROD-26 | 1 | 034 | ✅ `@AssumptionBased` |
| REQ-PROD-27 | 4 | 007, 065, 066, 067 | ✅ |
| REQ-PROD-28 | 4 | 008, 074, 076, 077 | ✅ |
| REQ-PROD-29 | 1 | 007 | ✅ |
| REQ-PROD-30 | 1 | 035 | 🟡 `@NeedsVerify` ở `c` (thư viện chưa có PDF) |
| REQ-PROD-31 | 2 | 036, 037 | ✅ Boundary |
| REQ-PROD-32 | 2 | 038, 039 | ✅ Boundary |
| REQ-PROD-33 | 1 | 040 | ✅ `@AssumptionBased` |
| REQ-PROD-34 | 2 | 041, 042 | ✅ `@KnownIssue` ở 041 |
| REQ-PROD-35 | 2 | 043, 044 | ✅ `@KnownIssue` ở 044 |
| REQ-PROD-36 | 2 | 045, 046 | ✅ `@KnownIssue` |
| REQ-PROD-37 | 1 | 047 | ✅ |
| REQ-PROD-38 | 1 | 048 | 🟡 Hiện diện; lưu ⏭️ Q14 |
| REQ-PROD-39 | 1 | 075 | ✅ |
| REQ-PROD-40 → 42 | 1 mỗi REQ | 049 · 050 · 051 | ✅ |
| REQ-PROD-43 | 1 | 052 | ✅ |
| REQ-PROD-44 | 2 | 003, 068 | ✅ |
| REQ-PROD-45 | 3 | 009, 065, 067 | ✅ |
| REQ-PROD-46 | 2 | 053, 054 | ✅ Negative |
| REQ-PROD-47 | 1 | 055 | ✅ `@KnownIssue` |
| REQ-PROD-48 | 1 | 056 | ✅ `@AssumptionBased` |
| REQ-PROD-49 | 3 | 010, 065, 072 | ✅ |
| REQ-PROD-50 | 1 | 057 | ✅ `@KnownIssue` |
| REQ-PROD-51 → 53 | 1 mỗi REQ | 004 · 005 · 011 | ✅ |
| REQ-PROD-54, 55, 57 | 1 mỗi REQ | 058 · 059 · 061 | ✅ `@KnownIssue` |
| REQ-PROD-56 | 1 | 060 | ✅ |
| REQ-PROD-58 | 1 | 062 | ✅ |
| REQ-PROD-59 | 1 | 063 | ✅; tải tệp ⏭️ Q14 |
| REQ-PROD-60 | 1 | 064 | 🟡 `@NeedsVerify` |

**60/60 REQ có TC.** Phép thử 6b: **đạt** — TC_048 ban đầu gánh REQ-PROD-38, 39 không có TC đỡ, đã tách TC_075.

## Bảng Đối Soát Evidence

| Ảnh evidence | Màn hình | TC dựa vào | Đầy đủ? |
|---|---|---|---|
| `product_list_sort_khong_tac_dung_viewport.jpg` | All products | 001, 006, 015, 017, 018 | ✅ |
| `product_create_required_bubble_viewport.jpg` | Add New Product | 002, 022, 023 | ⚠️ Chỉ phần đầu form; nhãn các khối dưới đọc từ DOM |
| `product_list_row_switches_on_zoom.png` | Dòng + công tắc | 019, 020, 021 | ✅ |
| `product_update_vn_loi_500_viewport.jpg` | Lỗi tab Vietnamese | 055 | ⚠️ Popup che; chữ lỗi đọc từ trang |
| (không có ảnh — đọc DOM / nguyên văn khi recon) | Edit Product · Digital · Bulk Upload · khối đỏ · thông báo xanh | 003, 004, 005, 024 → 027, 038, 053 → 062 | ✅ Câu chữ đọc nguyên văn |

### Vùng chưa có evidence — TC gắn `@NeedsVerify`

| TC | Vùng chưa quan sát | Đề xuất |
|---|---|---|
| 035c | Chọn PDF | Thư viện chưa có PDF — tải 1 tệp PDF test lên trước (PC đã ghi) |
| 064 | Nội dung tệp Bulk Export | Cần user cho phép tải tệp |

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC ID / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | TC_001–005 (bảng kiểm 5 + 5 + 5 + 4 + 4 mục) |
| 1 | Open form | ✅ | TC_006 (5 biến thể) |
| 1 | Display | ✅ | TC_001, 004, 007 |
| 1 | Input valid · Save · Verify data | ✅ | TC_007–011 |
| 2 | UI Behavior | ✅ | TC_010, 018, 019–021 |
| 2 | Required | ✅ | TC_022, 023, 030 |
| 2 | Validation | ✅ | TC_024–033, 036, 038, 041, 044–046, 053, 054, 058, 059, 061. Bảng Text cho Name, Unit, SKU: đủ required · max · whitespace · Unicode · XSS (069). Bảng Number cho giá, discount, min qty: âm · 0 · biên |
| 2 | Equivalence Partitioning | ✅ | TC_038, 041, 042 (Flat / Percent) |
| 2 | Boundary Value Analysis | ✅ | TC_025, 026, 037, 038, 039 |
| 2 | Business Rule | ✅ | TC_034, 040, 041, 045, 056 |
| 2 | Decision Table | ✅ | Discount × kiểu × giá: TC_038, 039, 040, 041, 042 |
| 2 | State Transition | ✅ | TC_020 (`Published`) |
| 2 | Dependency | ✅ | TC_031, 049–052 |
| 2 | Use Case / Scenario | ✅ | TC_065 |
| 2 | Save / Edit / Delete | ✅ | TC_007–010, 056, 057 |
| 2 | Tìm · lọc · sắp xếp | ✅ | TC_012, 015, 016, 017 |
| 2 | Error Guessing | ✅ | TC_066, 067 |
| 3 | Permission | ✅ + ⏭️ | TC_068. Role khác: ⏭️ chưa có tài khoản (AMB-PROD-16). **Quyết định: user, 02-11-2026 (Q14).** Rà lại khi được cấp tài khoản |
| 3 | Security | ✅ | TC_067, 068, 069, 070 |
| 3 | API · Database · Integration · Logging / Audit | ➖ | QA không có quyền — đội Dev xác minh (danh mục requirements) |
| 4 | Compatibility | ⏭️ Cố ý bỏ | Chỉ Chromium. **Quyết định: user, 02-11-2026.** Rà lại khi hệ thống cam kết hỗ trợ trình duyệt khác |
| 4 | Responsive / UI Stability | ✅ | TC_073 |
| 4 | Accessibility | ✅ | TC_072 |
| 4 | Localization | ✅ | TC_033, 055, 071 |
| 4 | Performance | ➖ | Không có ngưỡng thời gian; đo tải — đội hạ tầng |
| 4 | Regression | ➖ | Module mới, chưa có bug nào được đóng |
| 4 | E2E | ✅ | TC_065, 074, 076 (luồng liên module), 077 (đối chiếu Admin ↔ cửa hàng) |
| — | Bulk Import · Flash Deal (phần lưu) | ⏭️ Cố ý bỏ | Ghi hàng loạt / đổi dữ liệu có sẵn. **Quyết định: user, 02-11-2026 (Q14).** Rà lại khi có tệp mẫu và đợt Flash Deal test |

## Đối soát cột Automation

| Kết quả | Số TC | TC |
|---|---|---|
| Yes | 70 | Còn lại |
| Partial | 5 | 035 · 052 · 064 · 073 · 074 |
| No | 0 | — |

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_PROD_TC_035 | Web | Partial | 2 · dữ liệu tệp | Cần tệp mẫu trong Uploaded Files |
| AEC_PROD_TC_052 | Web | Partial | 2 · dữ liệu chéo module | Bảng tổ hợp sinh động theo dữ liệu màu, thuộc tính |
| AEC_PROD_TC_064 | Web | Partial | 2 · tệp tải về | Cần đọc tệp `.xlsx` |
| AEC_PROD_TC_073 | Web | Partial | 2 · Expected cần mắt người | Kiểm tay: chữ không đè nhau |
| AEC_PROD_TC_074 | Web | Partial | 2 · mặt cửa hàng | Cửa hàng có popup quảng cáo cần đóng |
| AEC_PROD_TC_035 · 064 | Web | ⏸️ Hoãn | — | `@NeedsVerify` — chạy tay lần đầu để chốt Expected rồi mới automate |

**Lưu ý automation:** `Delete`, `Duplicate` là liên kết GET — chỉ thao tác qua nút. `Tags` là ô thư viện, điền bằng script phải đúng định dạng thư viện, chuỗi thường gây trang lỗi. Chuỗi `<script>` ở tên sản phẩm cần dọn ngay sau TC_069.

**Thứ tự ưu tiên automate:** `@Smoke`/`@CriticalPath` (TC_001–011) → Validation/BVA (TC_022–027, 036–042) → phần còn lại.

### Automation đã có (04-11-2026)

Selenium Java · `src/test/java/com/aec/tests/` — `ProductListTest` · `ProductCreateTest` · `ProductPriceVariantTest` · `ProductEditTest` · `DigitalBulkProductTest` (nền `ProductTestBase`). Label `testId` = TC ID. Mỗi sản phẩm test có Name chứa mã `TC<nnn><biến thể>_<epoch>_<hex>`; dọn sau mỗi test: sản phẩm số ở Digital Products, sản phẩm thường ở All products, đều qua hộp xác nhận. Sản phẩm có thể bị lưu tên rỗng (TC_053, TC_061) ghi id, khi dọn đổi tên về mã truy vết rồi xoá.

| Phạm vi | TC |
|---|---|
| Đã automate | 001–063, 065–077 — **76 TC** (TC_035 chỉ biến thể `a`, `b`) |
| Không automate | **035-c** (thư viện chưa có PDF) · **064** (Bulk Export tải tệp `.xlsx` về máy chạy test) |
| Lệch nhỏ so với TC | TC_007, 011: sản phẩm mới kiểm trong 5 dòng đầu (5 luồng song song) · TC_017: đọc Base Price / Num of Sale ở cột Info để so thứ tự · TC_018-a, 074: nút View mở tab mới — chuyển sang tab đó để kiểm · TC_025, 026: Name bắt đầu bằng `P255_` / `P256_` + mã truy vết thay cho `AutoProd255_…` · TC_041-b: kiểm khối lỗi có nhắc tới `100` · TC_043, 044: đặt chuỗi ngày thẳng vào ô `Discount Date Range` như sau khi bấm Apply · TC_046: SKU bắt đầu bằng mã truy vết để không trùng giữa các lần chạy · TC_052: chọn 2 màu đầu và 2 giá trị **khác tên** đầu tiên của thuộc tính `Metal` đầu tiên trong danh sách (hệ thống có nhiều thuộc tính cùng tên `Metal`) · TC_057: dừng nếu kết quả tìm có dòng không chứa mã test, kiểm id đã tick trước khi bấm Delete selection — giữ trong automation theo quyết định của user 04-11-2026 (khác chạy tay: auto-skip) · TC_072-b: ghi thứ tự focus vào report, kiểm 4 lần Tab sang 4 ô khác nhau và có viền focus · TC_073: kiểm danh sách + form có đủ khối, chữ đè nhau kiểm tay |

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–067, 075 |
| Performance Efficiency | ➖ Không áp dụng | Không có ngưỡng — đội hạ tầng nếu cần đo tải |
| Compatibility | ➖ Ngoài phạm vi | Chỉ Chromium — user quyết 02-11-2026; liên kết dữ liệu với Category, Brand, Colors, Attribute ở TC_031, 049–052 |
| Interaction Capability | ✅ Có TC | TC_022, 023 · 010 (huỷ) · 072 |
| Reliability | ✅ Có TC | TC_066, 067 |
| Security | ✅ Có TC | TC_067–070 |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn, không kiểm bằng manual TC |
| Flexibility | ✅ Có TC | TC_033, 055 (đa ngôn ngữ) · 073 |
| Safety | ✅ Có TC | TC_010, 057 (xác nhận trước khi xoá) · 056 |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 5 biến thể/TC; TC thiết kế để FAIL tách riêng; khác trường không gộp |
| 1 | Unique TC ID | ✅ 75 ID, không trùng |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Mọi dữ liệu có giá trị cụ thể, `<ts>` có ví dụ `1793700000` |
| 4 | Field validation coverage | ✅ Chi tiết ở dòng `V2 · Validation` |
| 5 | Automation metadata | ✅ 75/75 có `Automation` · `Auto Type` · tag; 5 Partial có lý do |
| 6 | Requirement coverage | ✅ 60/60 REQ; 6b đạt sau khi tách TC_075 |
| 7 | Evidence-verified | ✅ Đã mở 4/4 ảnh; vùng chưa quan sát gắn `@NeedsVerify` (8 TC lúc sinh; còn 2 TC — 035, 064 — sau review 03-11-2026) |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không có thuật ngữ DOM hoặc mã HTTP ở phần TC chính. `419 \| PAGE EXPIRED` là chữ hiển thị trên trang |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴 |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 02-11-2026 | `/generate-testcases-manual-rbt product` (FULL RBT) | AEC_PROD_TC_001 → 075 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-PROD-01 → 60, độ hạt GỘP, rủi ro Cao, giả định Q1–Q14 |
| 03-11-2026 | `/review-testcases product` mode FIX ([báo cáo](review/testcase_review_report_web_20261103.md)) theo [run_1793607938](../../executions/product/web/run_1793607938/execution_report.md) | 025, 035, 047, 052, 063, 064, 067, 072, 073, 074 | 🟡 Sửa | TC_052 đổi Test Data (2 giá trị khác tên của `Metal`), TC_035 thêm PC tải PDF + Expected từng biến thể, TC_064 thêm PC được tải tệp, TC_067 thêm luật chấm BLOCKED. TC_025, TC_072 thêm `@KnownIssue`. Gỡ `@NeedsVerify` 6 TC (047, 052, 063, 072, 073, 074). Không có mốc git (thư mục chưa là repo) — bản trước khi sửa sao lưu ở thư mục tạm của phiên `backup_product_tc_20261103` |
| 03-11-2026 | User xác nhận môi trường **dùng chung** | Toàn bộ (dòng Môi trường ở index và part 01) | 🟡 Sửa | Đổi "không dùng chung" → "dùng chung", dọn dữ liệu ngay sau từng TC. Khi chạy bằng `/execute-test-cases`, TC xoá hàng loạt (TC_057) sẽ bị auto-skip |
| 04-11-2026 | `/generate-automation-web` mode TC | 74 TC (xem mục Automation đã có) | 🟢 Thêm | Sinh automation Selenium Java. Không đổi nội dung TC nào |
| 10-11-2026 | User yêu cầu: luồng liên module Login → Brand → Category → Product, đối chiếu chi tiết Admin ↔ cửa hàng (chế độ BỔ SUNG) | AEC_PROD_TC_076, 077 · PC-6 (part 01) | 🟢 Thêm | TC_076 luồng tạo trọn vẹn dùng Brand / Category vừa tạo; TC_077 đối chiếu trang sửa với trang `/product/<slug>` (tên, brand, giá, giá giảm, đơn vị, tồn kho, Min Qty, Description, Meta Title) và trang brand / danh mục ngoài cửa hàng. Khảo sát trang cửa hàng 10-11-2026: không hiện Category, Tags. Automation: `ProductCrossModuleTest` |
| 10-11-2026 | Automation `ProductCrossModuleTest` | AEC_PROD_TC_076, 077 | 🟡 Sửa | TC_077 gỡ `@NeedsVerify` và ghi chú ⚠️: chạy thật xác nhận ô số lượng ngoài cửa hàng mặc định bằng Min Qty (2). 076 PASS 3/3, 077 PASS 2/2 lần liên tiếp |
