# Báo Cáo Review Test Cases — Manage Category (web)

## Tổng quan

- **Nguồn:** [../web/test_cases_category_web.md](../web/test_cases_category_web.md) · index [../TEST_CASES_CATEGORY_SUMMARY.md](../TEST_CASES_CATEGORY_SUMMARY.md)
- **Mode:** FIX — phạm vi **có trọng tâm** theo yêu cầu user 29-10-2026: 6 TC FAIL cần đối chiếu (TC_003, 018, 019, 028, 033, 047) + 5 TC `@NeedsVerify` đã có kết quả chạy (TC_008, 015, 030, 034, 044). Không chấm lại toàn bộ 50 TC — bộ TC vừa qua Quality Gate 11/11 lúc sinh (29-10-2026)
- **Căn cứ đối chiếu:** [run_1793261923](../../../executions/category/web/run_1793261923/execution_report.md) · [requirements_category_web.md](../../../requirements/category/web/requirements_category_web.md) · [REQUIREMENTS_CATEGORY_SUMMARY.md](../../../requirements/category/REQUIREMENTS_CATEGORY_SUMMARY.md)
- **Số TC review:** 11 · **Kết quả:** 🟢 7 · 🟡 4 · 🔴 0 · **Điểm trung bình:** 10,9/12
- **Loại trừ theo requirements:** tự gửi dữ liệu tạo vòng lặp cha–con (⏭️ Q3) · phân quyền role khác (⏭️ AMB-CAT-08) — không chấm, không đề xuất
- **Lưu ý:** thư mục dự án **không phải repo git** → không có mốc git cho bản trước khi sửa (xem Checkpoint)

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_CAT_TC_003 | 9/12 | 🟡 | Tiêu chí 2, 5: mục `4` ghi *"Slug có sẵn giá trị dạng `<tên danh mục>-<5 ký tự chữ số>`"* — lệch REQ-CAT-27 (*"`<tên bỏ ký tự đặc biệt>-<5 ký tự chữ số>`"*). Chạy thật: `AutoCatTC0031793261923-ahh5d` — đúng REQ, sai TC | Mục `4`: *"Ô Slug có sẵn giá trị dạng `<Name đã bỏ ký tự đặc biệt, kể cả `_`>-<5 ký tự chữ số>`, VD Name `AutoCat_TC003_1793200000` → `AutoCatTC0031793200000-ab1Cd`"* |
| AEC_CAT_TC_033 | 9/12 | 🟡 | Biến thể `a` cùng lỗi: *"Slug dạng `AutoCatSlug_TC033a_1793200000-<5 ký tự chữ số>` hoặc bản chữ thường của nó"* — giữ `_`, trái REQ-CAT-27 | `a`: *"Slug `AutoCatSlugTC033a1793200000-<5 ký tự chữ số>` (dấu `_` bị bỏ)"*. `b` giữ nguyên (đã PASS) |
| AEC_CAT_TC_028 | 9/12 | 🟡 | Biến thể `f` ghi *"4. `2`"* cho `1.5` — chép từ AMB-CAT-10 (máy chủ làm tròn khi nhận request), nhưng TC thao tác trên **giao diện**: ô là ô số bước 1, trình duyệt chặn gửi form. Expected không thể xảy ra qua đường TC mô tả | `f`: *"3. Không gửi form; ô Ordering Number hiện bong bóng `Please enter a valid value. The two nearest valid values are 1 and 2.`; không có danh mục mới"*. Ghi chú: hành vi làm tròn của máy chủ (AMB-CAT-10) chỉ tới được bằng request trực tiếp — ngoài phạm vi TC giao diện |
| AEC_CAT_TC_008 | 11/12 | 🟢 | Tiêu chí 2: bước 4 ghi *"thông báo xanh báo xoá thành công (câu chữ chưa quan sát — ghi nguyên văn khi chạy)"* — đã quan sát được | Bước 4: *"hiện thông báo xanh `Category has been deleted successfully`; trang về `All categories`"*. Gỡ `@NeedsVerify` |
| AEC_CAT_TC_015 | 11/12 | 🟢 | Tag `@NeedsVerify` đã cũ — `a` `Nothing found` (AMB-CAT-11), `b`, `c` đều đã xác nhận trên UI | Gỡ `@NeedsVerify`. Giữ `@AssumptionBased` |
| AEC_CAT_TC_030 | 12/12 | 🟢 | Tag `@NeedsVerify` đã cũ — hộp thư viện, `1 File selected`, nút × đã chạy thật | Gỡ `@NeedsVerify` |
| AEC_CAT_TC_034 | 12/12 | 🟢 | Tag `@NeedsVerify` đã cũ — `a` chữ thường, `b` tự sinh lại `AutoCatTC0341793261923-ATPLZ` | Gỡ `@NeedsVerify`. Có thể cụ thể hoá `b`: *"Ô Slug được sinh lại từ Name theo REQ-CAT-27"* |
| AEC_CAT_TC_044 | 11/12 | 🟢 | Tiêu chí 2: bước 3 *"được đưa về trang đăng nhập hoặc báo phiên hết hạn"* — hai khả năng; đã quan sát: trang `419 \| PAGE EXPIRED` | Bước 3: *"Hiện trang `419 \| PAGE EXPIRED` (hoặc trang đăng nhập); không có thông báo thành công"*. Gỡ `@NeedsVerify` |
| AEC_CAT_TC_047 | 11/12 | 🟢 | Biến thể `c` (Esc đóng hộp xoá) FAIL; Expected là chuẩn trợ năng thông dụng của hộp thoại, không mâu thuẫn REQ-CAT-15 — **TC đúng, hệ thống lệch** | Giữ nguyên Expected. Gỡ `@NeedsVerify` → kết quả FAIL là lỗi thật (Low), báo bug được |
| AEC_CAT_TC_018 | 10/12 | 🟡 | Expected *"hoặc chặn… hoặc xoá kèm con"* dựa trên giả định; AMB-CAT-05 vẫn ❓ chờ PO. Chạy thật: xoá cha → con về gốc (`—`, Level 0). Expected cấm đúng trường hợp này. **Không xác định được TC sai hay hệ thống sai** khi chưa có quyết định | **Không sửa Expected** khi PO chưa chốt AMB-CAT-05. Ghi kết quả quan sát vào AMB-CAT-05 (qua `/update-requirements-from-ticket` hoặc khi PO trả lời). PO chọn "đưa con về gốc là đúng" → sửa Expected theo đó; chọn khác → báo bug |
| AEC_CAT_TC_019 | 10/12 | 🟢 | Như TC_018 — chạy thật: xoá ngay, chỉ toast chung, sản phẩm mất danh mục (bước 4 đạt). Phụ thuộc AMB-CAT-05 | **Không sửa** khi PO chưa chốt. Đây là rủi ro dữ liệu Product → nên đẩy AMB-CAT-05 lên ưu tiên cao |

> TC_018 xếp 🟡 vì Expected bước 4 phủ định (*"Không được xảy ra…"*) — không nói kết quả mong đợi cụ thể là gì; khi PO chốt nên viết lại dạng khẳng định.

## Phát hiện thêm ngoài danh sách (đề xuất, chưa sửa)

| TC ID | Vấn đề | Đề xuất |
|---|---|---|
| AEC_CAT_TC_042 | Biến thể `a`, `c` dùng chuỗi trùng tên danh mục **có sẵn** trên môi trường (id 3143 tên `<script>alert(1)</script>`; một danh mục tên `' OR '1'='1`) → `a` không thể ra `Nothing found` dù hệ thống đúng. Tiêu chí 4 (Test data) | Đổi Test Data: `a` `<script>alert(1)</script>zzq<ts>` · `b` `"><img src=x onerror=alert(1)>zzq<ts>` · `c` `' OR '1'='1 zzq<ts>` — hậu tố riêng để không khớp dữ liệu có sẵn |
| AEC_CAT_TC_011 | Biến thể `b` BLOCKED vì danh mục cũ Order Level `2147483647` | Không sửa TC — dọn dữ liệu 4544–4552 rồi chạy lại |

## Đối soát loại kiểm thử (4 vòng)

Giữ nguyên bảng 4 vòng ở index (Quality Gate 29-10-2026). Kết quả chạy chỉ làm thay đổi 2 điểm:

| Vòng | Nhánh | Trạng thái | Ghi chú |
|---|---|---|---|
| 4 | Accessibility | 🟡 Nông | TC_047 chưa có mục kiểm nút `Browse` (Banner/Icon) nhận focus bằng Tab — chạy thật thấy **không** nhận focus. Đề xuất bổ sung mục vào TC_047 biến thể `b` khi có quyết định về trợ năng |
| 2 | Business Rule — xoá có ràng buộc | 🟡 | Phụ thuộc AMB-CAT-05 (TC_018, 019) |

## Coverage Gaps

| # | Kịch bản thiếu | Vòng / Nhánh | Priority đề xuất |
|---|---|---|---|
| 1 | Nút `Browse` của Banner, Icon với tới được bằng bàn phím | V4 · Accessibility | Low |

> Hành vi mới thấy khi chạy (form sửa nhận Name toàn khoảng trắng — TC_022b; emoji thành `?` — TC_048b; Slug bỏ chữ có dấu) **đã có TC phủ** và đã FAIL đúng — không thiếu TC, chỉ cần báo bug.

## TC trùng lặp

Không phát hiện trong phạm vi review.

## Kết luận & Khuyến nghị

1. **Sửa 3 TC viết sai** (TC_003, 033a, 028f) — sau khi sửa, ba FAIL này sẽ thành PASS ở lần chạy lại; **không** báo bug.
2. **Gỡ `@NeedsVerify`** cho TC_008, 015, 030, 034, 044, 047; cụ thể hoá Expected của TC_008, 044 theo câu chữ đã quan sát.
3. **TC_047c là lỗi thật** (Esc không đóng hộp xoá) — báo bug mức Low.
4. **TC_018, 019 chờ PO chốt AMB-CAT-05** — không sửa TC, không báo bug cho tới khi có quyết định; ghi quan sát vào AMB-CAT-05.
5. Cân nhắc sửa Test Data TC_042 để tránh trùng dữ liệu có sẵn.
