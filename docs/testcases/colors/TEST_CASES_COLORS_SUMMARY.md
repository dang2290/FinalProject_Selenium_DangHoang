# Test Cases — Colors (tổng 49 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_COLORS_SUMMARY.md](../../requirements/colors/REQUIREMENTS_COLORS_SUMMARY.md) · web: [requirements_colors_web.md](../../requirements/colors/web/requirements_colors_web.md) |
| Mode · độ hạt | FULL RBT · **GỘP** — 49 TC · 69 ca kiểm (15 TC có bảng biến thể, 35 biến thể) |
| Dải TC ID đã dùng | `AEC_COLOR_TC_001` → `AEC_COLOR_TC_049` — mã kế tiếp `AEC_COLOR_TC_050` |
| Phạm vi REQ | `REQ-COLOR-01` → `REQ-COLOR-30` (30/30). REQ-COLOR-30: chỉ kiểm hiện diện công tắc, tác dụng ⏭️ (Q9) |
| Mức rủi ro · độ sâu | `Cao` → **Đầy đủ** — xem đầu file web. **Nâng thêm khi:** có tài khoản role khác · PO cho phép bấm công tắc lọc màu · PO chốt định dạng mã màu |
| Giả định đang áp | Q1–Q11 (≈ AMB-COLOR-01 → 12) — user chấp nhận toàn bộ 02-11-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Môi trường | **Dùng chung** (user xác nhận 03-11-2026) — chỉ đụng dữ liệu do TC tạo. **Không bấm** công tắc `Color filter activation` (PC-4). TC_037 tạo 1 sản phẩm test, dọn ngay sau TC |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/test_cases_colors_web.md](web/test_cases_colors_web.md) | V1 Smoke · V2a Danh sách & tìm kiếm · V2b Tạo · V2c Sửa · V2d Xoá & liên kết · V3 · V4 | 49 | 001–049 | REQ-COLOR-01 → 30 |

## Bảng Đối Soát Coverage

| REQ ID | Mô tả ngắn | Số TC | TC IDs | Đủ Positive/Negative/Boundary? |
|---|---|---|---|---|
| REQ-COLOR-01 | Mở trang All Colors | 4 | 001, 003, 043, 049 | ✅ Positive + Negative (chưa đăng nhập) |
| REQ-COLOR-02 | Các cột của bảng | 2 | 001, 008 | ✅ Positive |
| REQ-COLOR-03 | Phân trang | 1 | 008 | ✅ Positive |
| REQ-COLOR-04 | Mới nằm đầu bảng | 2 | 004, 040 | ✅ Positive |
| REQ-COLOR-05 | Hai nút thao tác | 2 | 001, 009 | ✅ Positive |
| REQ-COLOR-06 | Tìm theo tên | 4 | 005, 010, 011, 045 | ✅ Positive + Negative (bảo mật) |
| REQ-COLOR-07 | Không có kết quả | 2 | 012, 045 | ✅ Negative |
| REQ-COLOR-08 | Form Add New Color | 3 | 001, 048, 049 | ✅ Positive + bàn phím |
| REQ-COLOR-09 | Ô bắt buộc phía trình duyệt | 2 | 013, 014 | ✅ Negative |
| REQ-COLOR-10 | Tạo thành công | 4 | 004, 040, 041, 042 | ✅ Positive + Negative (double submit, hết phiên) |
| REQ-COLOR-11 | Name toàn khoảng trắng khi tạo | 1 | 017 | ✅ Negative |
| REQ-COLOR-12 | Code toàn khoảng trắng | 2 | 023, 031 | ✅ Negative |
| REQ-COLOR-13 | Mã duy nhất | 2 | 024, 031 | ✅ Negative |
| REQ-COLOR-14 | Mã tối đa 255 | 2 | 025, 026 | ✅ Boundary 255 (`@KnownIssue`) / 256 |
| REQ-COLOR-15 | Định dạng mã | 2 | 027, 028 | ✅ Positive + Negative (`@KnownIssue`) |
| REQ-COLOR-16 | Bỏ khoảng trắng đầu/cuối | 2 | 018, 019 | ✅ Boundary |
| REQ-COLOR-17 | Dấu cách giữa Name | 2 | 020, 034 | ✅ (`@AssumptionBased`) |
| REQ-COLOR-18 | Name tối đa 30 | 3 | 015, 016, 033 | ✅ Boundary 1/29/30 + 31/255 (`@KnownIssue`) |
| REQ-COLOR-19 | Trùng tên | 1 | 021 | ✅ (`@AssumptionBased`) |
| REQ-COLOR-20 | Unicode | 2 | 022, 047 | ✅ Positive + emoji (`@KnownIssue`) |
| REQ-COLOR-21 | HTML ở trang Colors như chữ | 1 | 044 | ✅ Negative (bảo mật) |
| REQ-COLOR-22 | Trang sửa | 2 | 002, 043 | ✅ Positive + Negative |
| REQ-COLOR-23 | Lưu sửa | 4 | 006, 032, 040, 042 | ✅ Positive + Negative (hết phiên) |
| REQ-COLOR-24 | Kiểm định khi sửa | 4 | 029, 031, 033, 034 | ✅ Negative |
| REQ-COLOR-25 | Sửa Name toàn khoảng trắng | 1 | 030 | ✅ Negative (`@KnownIssue`) |
| REQ-COLOR-26 | Hộp xác nhận xoá | 3 | 007, 035, 048 | ✅ Positive + Cancel + Esc |
| REQ-COLOR-27 | Xoá thành công | 4 | 007, 036, 037, 040 | ✅ Positive + màu đang dùng (`@AssumptionBased` — AMB-COLOR-08) |
| REQ-COLOR-28 | Nguồn ô Colors của Product | 1 | 038 | ✅ Positive |
| REQ-COLOR-29 | Tên như chữ ở form sản phẩm | 1 | 046 | ✅ Negative (`@KnownIssue`) |
| REQ-COLOR-30 | Công tắc lọc màu | 1 | 039 | ✅ Hiện diện. Tác dụng ⏭️ Q9 |

**30/30 REQ có TC.** Phép thử 6b (TC gánh ≥ 2 REQ không có TC khác chống lưng): **không vi phạm** — mỗi TC nhiều REQ đều có TC khác chống lưng từng REQ (VD TC_001: REQ-02 ở 008, REQ-05 ở 009, REQ-08 ở 048; TC_004: REQ-04 ở 040).

## Bảng Đối Soát Evidence

| Ảnh evidence | Màn hình / trạng thái | TC dựa vào | Đầy đủ? |
|---|---|---|---|
| `colors_list_default_viewport.jpg` | All Colors — mặc định | 001, 003, 004, 005, 008, 009, 013, 014, 039 | ✅ |
| `colors_edit_default_viewport.jpg` | Color Information | 002, 006, 029 → 034 | ✅ |
| `colors_create_duplicate_code_error_viewport.jpg` | Lỗi trùng mã | 024, 017, 023, 026 (cùng khối đỏ) | ✅ |
| `colors_delete_confirmation_viewport.jpg` | Hộp `Delete Confirmation` | 007, 009, 035, 036 | ✅ |
| `product_color_select_html_name_zoom.png` | Ô `Colors` — tên HTML | 038, 046 | ✅ |
| (không có ảnh — đọc DOM / nguyên văn khi recon) | Khối đỏ `The name field is required.`, `The code field is required.`, `…255 characters.` · thông báo xanh · trang sau khi xoá | 017, 023, 026, 006, 007, 036 | ✅ Câu chữ đọc nguyên văn |

### Vùng chưa có evidence — TC gắn `@NeedsVerify`

Không còn. 5 vùng ban đầu (TC_025, 029, 037, 048, 049b) đã quan sát thật ở [run_1793589946](../../executions/colors/web/run_1793589946/execution_report.md) — gỡ `@NeedsVerify` 02-11-2026 theo [review](review/testcase_review_report_web_20261102.md).

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC ID / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | TC_001, 002 (bảng kiểm 6 + 4 mục) |
| 1 | Open form | ✅ | TC_003 (3 biến thể) |
| 1 | Display | ✅ | TC_001, 004, 005 |
| 1 | Input valid · Save · Verify data | ✅ | TC_004, 006, 007 |
| 2 | UI Behavior | ✅ | TC_009 (2 biến thể) · TC_035 |
| 2 | Required | ✅ | TC_013, 014, 029 |
| 2 | Validation | ✅ | Name: TC_015–018, 020, 022, 030, 033, 034 · Code: TC_019, 023–028, 031. Bảng Text: đủ 8/8 mục cho từng trường — required · min/max · whitespace-only · ký tự đặc biệt · XSS & SQL (044 ở V3) · Unicode/Emoji (022, 047) · khoảng trắng đầu/cuối (018, 019) |
| 2 | Equivalence Partitioning | ✅ | TC_027, 028 (định dạng hợp lệ / sai) · 015, 016 |
| 2 | Boundary Value Analysis | 🟡 | TC_015 (1 · 29 · 30) · 016 (31 · 255) · 025, 026 (255 · 256). Color Code có ngưỡng lưu thật ở 10 ký tự, chưa có TC 10 · 11 — chờ PO chốt giới hạn mã (review 02-11-2026) |
| 2 | Business Rule | ✅ | TC_024 (mã duy nhất) · 021 (trùng tên) · 032 (giữ mã của chính nó) |
| 2 | Decision Table | ➖ | Không có tổ hợp ≥ 3 điều kiện cho một kết quả |
| 2 | State Transition | ➖ | Màu không có trạng thái |
| 2 | Dependency | ✅ | TC_037, 038 (Product) |
| 2 | Use Case / Scenario | ✅ | TC_040 |
| 2 | Save / Edit / Delete | ✅ | TC_004, 006, 007, 032, 035, 036 |
| 2 | Tìm kiếm · phân trang | ✅ | TC_005, 008, 010, 011, 012 |
| 2 | Error Guessing | ✅ | TC_041 (Save hai lần) · 042 (hết phiên, 2 biến thể) |
| 3 | Permission | ✅ + ⏭️ | TC_043 (chưa đăng nhập, 2 biến thể). Role khác: ⏭️ chưa có tài khoản (AMB-COLOR-10). **Quyết định: user, 02-11-2026 (Q10).** Rà lại khi được cấp tài khoản role khác |
| 3 | Security | ✅ | TC_042, 043, 044, 045, 046 |
| 3 | API | ➖ | QA không có quyền gọi API — đội Dev xác minh (danh mục requirements) |
| 3 | Database | ➖ | QA không có quyền truy vấn CSDL — đội Dev xác minh |
| 3 | Integration | ➖ | Không có tích hợp bên thứ ba — liên kết Product kiểm ở V2 · Dependency |
| 3 | Logging / Audit | ➖ | QA chưa có quyền xem nhật ký; requirements không có yêu cầu audit |
| 4 | Compatibility | ⏭️ Cố ý bỏ | Chỉ kiểm Chromium, giống BRAND, CAT, ATTR. **Quyết định: user, 02-11-2026 (duyệt Bước 4).** Rà lại khi hệ thống cam kết hỗ trợ trình duyệt khác |
| 4 | Responsive / UI Stability | ✅ | TC_049 (1920 · 1366) |
| 4 | Accessibility | ✅ | TC_048 (3 biến thể) |
| 4 | Localization | ✅ | TC_022, 047 |
| 4 | Performance | ➖ | Trang 10 dòng, không có ngưỡng thời gian; đo tải thật — đội hạ tầng |
| 4 | Regression | ➖ | Module mới, chưa có bug nào được đóng |
| 4 | E2E | ✅ | TC_038, 040 |
| — | Công tắc lọc màu | ✅ + ⏭️ | TC_039 (hiện diện). Tác dụng ở cửa hàng: ⏭️ không bấm, cấu hình toàn hệ thống. **Quyết định: user, 02-11-2026 (Q9).** Rà lại khi PO cho phép |

## Đối soát cột Automation

| Kết quả | Số TC | TC |
|---|---|---|
| Yes | 45 | Còn lại |
| Partial | 4 | 037 · 038 · 046 · 049 |
| No | 0 | — |

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_COLOR_TC_037 | Web | Partial | 2 · dữ liệu chéo module | Cần Page Object Product để tạo sản phẩm test |
| AEC_COLOR_TC_038 | Web | Partial | 2 · dữ liệu chéo module | Cần Page Object Product (ô `Colors`). Màu ô mẫu kiểm tay |
| AEC_COLOR_TC_046 | Web | Partial | 2 · Expected cần mắt người | Tự động: mục có thẻ in đậm hay không. Kiểm tay: chữ hiện nguyên văn |
| AEC_COLOR_TC_049 | Web | Partial | 2 · Expected cần mắt người | Tự động: không cuộn ngang, nút bấm trúng. Kiểm tay: chữ không đè nhau |
| AEC_COLOR_TC_037 | Web | ⏸️ Hoãn | — | Chờ PO chốt AMB-COLOR-08. (TC_025, 029, 048, 049 hết hoãn — Expected đã chốt qua lần chạy 02-11-2026) |

**Lưu ý automation:** Delete là liên kết GET (`/admin/colors/destroy/<id>`) — automation **chỉ** xoá qua hộp xác nhận, không mở thẳng đường dẫn. Color Code phải duy nhất toàn hệ thống: sinh mã từ `<ts>`, chạy song song không được dùng chung mã.

**Thứ tự ưu tiên automate:** `@Smoke`/`@CriticalPath` (TC_001–007) → Validation/BVA (TC_013–019, 023–028) → phần còn lại.

### Automation đã có (04-11-2026)

Selenium Java · `src/test/java/com/aec/tests/` — `ColorListTest` · `ColorCreateTest` · `ColorEditTest` · `ColorLifecycleTest` (nền `ColorTestBase`). Label `testId` = TC ID. Mỗi màu test mang mã `TC<nnn><biến thể>_<epoch>_<hex>` trong Name (≤ 30 ký tự, không dấu cách); Color Code sinh ngẫu nhiên `#<a–f><5 hex>`. Dọn sau mỗi test: tìm theo mã → xoá qua hộp xác nhận. Màu có tên không chứa được mã (TC_015-a `Q`, TC_044-a payload 25 ký tự, TC_030 khi tên bị lưu rỗng) được xác định bằng **Color Code** ở các dòng đầu, ghi **id**, khi dọn đổi tên về mã truy vết rồi xoá.

| Phạm vi | TC |
|---|---|
| Đã automate | 001–036, 038–049 — **48 TC** |
| Không automate | **037** — phải lưu một sản phẩm thật (`Save & Unpublish`) trên môi trường dùng chung; đang ⏸️ Hoãn chờ AMB-COLOR-08 |
| Lệch nhỏ so với TC | Dữ liệu: Name dùng tiền tố ngắn (`Col_`, `Trim_`…) + mã truy vết thay cho `AutoColor_TC<nnn>_<ts>` (giới hạn 30 ký tự); Color Code ngẫu nhiên thay cho `#<TC><ts>` · TC_004, 040, 048-a: màu mới kiểm trong 5 dòng đầu (5 luồng song song) · TC_008: không so tổng cố định 299, kiểm số `#` liên tục ở trang cuối · TC_013, 014: kiểm form không gửi + bong bóng nhắc, không đếm số dòng · TC_016, 028: kiểm "có khối lỗi + không có dòng chứa mã test" (chưa có câu lỗi nguyên văn) · TC_017: không kiểm "không có màu mới" (tên rỗng không truy vết được) · TC_027-a: mã 3 ký tự ngẫu nhiên, trùng thì thử lại ≤ 5 lần · TC_028: thêm hậu tố ngẫu nhiên vào mã sai định dạng (`red<hex>`, `c28<hex>`, `#GG<hex>`) để không trùng giữa các lần chạy · TC_039: ghi trạng thái công tắc vào report, không coi khác "tắt" là lỗi · TC_043-b, TC_045: dùng id `1` / từ khoá có hậu tố thời gian · TC_044, 046: payload rút gọn cho vừa 30 ký tự (`<b>B</b>…`, `'OR'1'='1--…`) |

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–042 |
| Performance Efficiency | ➖ Không áp dụng | Trang 10 dòng, không có ngưỡng — đội hạ tầng nếu cần đo tải |
| Compatibility | ➖ Ngoài phạm vi | Chỉ Chromium — user quyết 02-11-2026; liên kết dữ liệu với Product đã ở TC_037, 038 |
| Interaction Capability | ✅ Có TC | TC_013, 014, 029 (chặn nhập trống) · 035 (huỷ) · 048 (bàn phím) |
| Reliability | ✅ Có TC | TC_041 (Save hai lần) · 042 (hết phiên) |
| Security | ✅ Có TC | TC_043–046 |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn, không kiểm bằng manual TC |
| Flexibility | ✅ Có TC | TC_049 (1920 · 1366) · 022 (Unicode) |
| Safety | ✅ Có TC | TC_007, 035 (xác nhận trước khi xoá) · 037 (xoá màu đang dùng) |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 3 biến thể/TC; TC thiết kế để FAIL (016, 025, 028, 030, 033, 046, 047) tách riêng, mỗi TC một lỗi; Name và Color Code không gộp chung TC |
| 1 | Unique TC ID | ✅ 49 ID, không trùng, đúng mẫu `AEC_COLOR_TC_<3 số>` |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected đánh số theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Mọi dữ liệu có giá trị cụ thể; `<ts>` có ví dụ `1793500000`; Name ≤ 30 ký tự, Color Code theo mẫu duy nhất |
| 4 | Field validation coverage | ✅ 2 trường Text (Name, Color Code) — đủ 8/8 mục bảng Text, chi tiết ở dòng `V2 · Validation`. Ô tìm kiếm: TC_010, 012, 045 |
| 5 | Automation metadata | ✅ 49/49 có `Automation` · `Auto Type` · tag; 4 Partial có lý do ở mục Đối soát cột Automation |
| 6 | Requirement coverage | ✅ 30/30 REQ; 6b đạt |
| 7 | Evidence-verified | ✅ Đã mở 5/5 ảnh; 5 TC từng gắn `@NeedsVerify` đã có kết quả chạy thật, gỡ tag 02-11-2026 |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không có thuật ngữ DOM hoặc mã HTTP ở phần TC chính. `419 \| PAGE EXPIRED` là chữ hiển thị trên trang |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm, ô `➖` có lý do và người chịu trách nhiệm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴; `UI cơ bản` · `Validation` · `Permission` đều ✅ |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 02-11-2026 | `/generate-testcases-manual-rbt colors` (FULL RBT) | AEC_COLOR_TC_001 → 049 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-COLOR-01 → 30, độ hạt GỘP, rủi ro Cao, giả định Q1–Q11 |
| 02-11-2026 | `/review-testcases colors` mode FIX — [báo cáo](review/testcase_review_report_web_20261102.md) | AEC_COLOR_TC_025, 029, 037, 048, 049 | 🟡 Sửa | TC_037: thay Expected bằng hành vi đã quan sát, giữ `@AssumptionBased` theo AMB-COLOR-08 · TC_025: đổi `@NeedsVerify` thành `@KnownIssue`, Expected giữ nguyên · gỡ `@NeedsVerify` + ghi chú ⚠️ ở TC_029, 048, 049. **Mốc trước khi sửa:** dự án chưa có git — bản cũ sao lưu ở thư mục tạm của phiên (`backup_color_review_20261102`) |
| 04-11-2026 | `/generate-automation-web` mode TC | 48 TC (xem mục Automation đã có) | 🟢 Thêm | Sinh automation Selenium Java. Không đổi nội dung TC nào |
