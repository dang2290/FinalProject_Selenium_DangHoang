# Báo Cáo Review Test Cases — Colors (web)

## Tổng quan

- **Nguồn:** [../web/test_cases_colors_web.md](../web/test_cases_colors_web.md) · index [../TEST_CASES_COLORS_SUMMARY.md](../TEST_CASES_COLORS_SUMMARY.md)
- **Mode:** FIX — phạm vi **có trọng tâm**, giống review Attribute: 5 TC `@NeedsVerify` đã có kết quả chạy (TC_025, 029, 037, 048, 049). Không chấm lại toàn bộ 49 TC — bộ TC vừa qua Quality Gate 11/11 lúc sinh (02-11-2026). 6 TC `@KnownIssue` FAIL đúng dự kiến, không cần xét lại
- **Căn cứ đối chiếu:** [run_1793589946](../../../executions/colors/web/run_1793589946/execution_report.md) · [requirements_colors_web.md](../../../requirements/colors/web/requirements_colors_web.md) · [REQUIREMENTS_COLORS_SUMMARY.md](../../../requirements/colors/REQUIREMENTS_COLORS_SUMMARY.md)
- **Số TC review:** 5 · **Kết quả:** 🟢 4 · 🟡 1 · 🔴 0 · **Điểm trung bình:** 10,8/12
- **Loại trừ theo requirements:** phân quyền role khác (⏭️ AMB-COLOR-10) · tác dụng công tắc lọc màu (⏭️ AMB-COLOR-09) · trình duyệt khác Chromium (⏭️ user quyết 02-11-2026) — không chấm, không đề xuất
- **Lưu ý:** thư mục dự án **không phải repo git** → không có mốc git; bản trước khi sửa sẽ sao lưu ra thư mục tạm của phiên

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_COLOR_TC_037 | 9/12 | 🟡 | Tiêu chí 2: bước 2 *"Có thông báo kết quả rõ ràng, ghi nguyên văn khi chạy"* không đo được. Chạy thật: xoá không bị chặn, `Color has been deleted successfully`; ô `Colors` về `Nothing selected` | Bước 2: *"Hiện `Color has been deleted successfully`, không có cảnh báo hay chặn xoá"* · bước 3: thêm *"tiêu đề `Edit Product`"* · bước 4: *"Ô `Colors` hiện `Nothing selected`, không có mục trống"*. Ghi chú: Expected theo hành vi quan sát 02-11-2026, AMB-COLOR-08 chưa chốt. Gỡ `@NeedsVerify`, giữ `@AssumptionBased` |
| AEC_COLOR_TC_025 | 10/12 | 🟢 | Expected *"đủ 255 ký tự"* khớp thông báo của máy chủ (*"may not be greater than 255"*), chạy thật chỉ lưu 10 ký tự — **TC đúng, hệ thống lệch**. Phần còn lại cũ: ghi chú `⚠️ chưa có evidence`, tag `@NeedsVerify` | **Giữ Expected.** Bỏ ghi chú ⚠️, đổi `@NeedsVerify` thành `@KnownIssue`, thêm dòng *"Hiện tại mã bị cắt còn 10 ký tự (run_1793589946) — chờ mở AMB"*. Mở AMB mới và cập nhật REQ-COLOR-14 bằng `/update-requirements-from-ticket` |
| AEC_COLOR_TC_048 | 11/12 | 🟢 | Biến thể `b`, `c` FAIL, là chuẩn trợ năng thông dụng, không mâu thuẫn REQ-COLOR-08, 26 — **TC đúng, hệ thống lệch** (cùng kết luận với Attribute TC_040) | Giữ Expected. Gỡ `@NeedsVerify`, bỏ ghi chú ⚠️ ở `c`. FAIL là lỗi thật mức Low |
| AEC_COLOR_TC_029 | 12/12 | 🟢 | Tag `@NeedsVerify` + ghi chú `⚠️ chưa có evidence` đã cũ — chạy thật đúng Expected | Gỡ tag, bỏ ghi chú |
| AEC_COLOR_TC_049 | 12/12 | 🟢 | Như trên — 1920 và 1366 đều đạt | Gỡ tag, bỏ ghi chú ở `b` (cả câu nhắc lỗi Attribute) |

> ✅ **Đã áp dụng 02-11-2026** (user duyệt toàn bộ): TC_037 viết lại Expected, TC_025 đổi sang `@KnownIssue`, gỡ `@NeedsVerify` + ghi chú ⚠️ ở TC_029, 037, 048, 049; index và danh mục đã đồng bộ.

## Phát hiện thêm ngoài danh sách (đề xuất, chưa sửa)

| TC ID | Vấn đề | Đề xuất |
|---|---|---|
| AEC_COLOR_TC_042 | Expected *"`419 \| PAGE EXPIRED` hoặc được đưa về trang đăng nhập"* có hai khả năng; lần này cả 2 biến thể đều ra `419`. Hai khả năng là cùng một kết quả (không lưu), không trừ điểm | Không sửa |
| AEC_COLOR_TC_044 | Biến thể `a` đưa chuỗi `<script>` vào CSDL; chuỗi lọt vào ô chọn màu ở form sản phẩm nếu chưa dọn (AMB-COLOR-05) | Khi chạy, dọn ngay sau TC trước khi mở form sản phẩm (PC-3 đã yêu cầu dọn sau TC) |

## Đối soát loại kiểm thử (4 vòng)

Giữ nguyên bảng 4 vòng ở index. Kết quả chạy chỉ làm thay đổi 1 điểm:

| Vòng | Nhánh | Trạng thái | Ghi chú |
|---|---|---|---|
| 2 | Boundary Value Analysis | 🟡 | Color Code có ngưỡng lưu thật ở 10 ký tự, bộ TC chỉ phủ 255 · 256 |

## Coverage Gaps

| # | Kịch bản thiếu | Vòng / Nhánh | Priority đề xuất |
|---|---|---|---|
| 1 | Color Code 10 ký tự lưu đủ · 11 ký tự (biên thật) | V2 · BVA | Medium — sinh sau khi PO chốt giới hạn mã, qua `/update-testcases-from-impact` |

## TC trùng lặp

Không phát hiện trong phạm vi review.

## Kết luận & Khuyến nghị

1. **Sửa TC_037** (Expected bước 2–4 theo hành vi đã quan sát).
2. **TC_025 đổi sang `@KnownIssue`**; **gỡ `@NeedsVerify`** cho TC_029, 037, 048, 049.
3. **TC_025, 048b, 048c là lỗi thật** — báo bug được; 048 gộp bug chung 4 module.
4. **Cập nhật requirements** trước khi mở rộng TC: REQ-COLOR-14 (ngưỡng 10), AMB mới, AMB-COLOR-08 (kết quả TC_037).
