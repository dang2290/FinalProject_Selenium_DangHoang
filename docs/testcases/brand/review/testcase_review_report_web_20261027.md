# Báo Cáo Review Test Cases — Manage Brand · Web

## Tổng quan
- **Nguồn:** [web/test_cases_brand_web.md](../web/test_cases_brand_web.md) · index [TEST_CASES_BRAND_SUMMARY.md](../TEST_CASES_BRAND_SUMMARY.md)
- **Mode:** FIX — phạm vi do user chỉ định: chấm lại **TC_008, TC_034, TC_041** + dọn `@NeedsVerify` / `⚠️ chưa có evidence` theo [execution report run_1793105365](../../../executions/brand/web/run_1793105365/execution_report.md). Không chấm lại 47 TC còn lại, không chạy lại đối soát 4 vòng / 15 loại field (giữ nguyên kết quả Quality Gate ngày 27-10-2026)
- **Số TC review:** 3
- **Kết quả:** 🟢 2 tốt (cần cập nhật theo evidence mới) | 🟡 1 cần sửa | 🔴 0
- **Điểm trung bình:** 10,3/12
- **Loại trừ theo requirements:** Không có
- **Mốc trước khi sửa:** thư mục dự án **không phải git repository** → không có mốc git để lấy lại bản cũ

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_BRAND_TC_008 | 9/12 (Clarity 2 · Expected 1 · Độc lập 2 · Data **0** · Trace 2 · Focus 2) | 🟡 | Test Data `Tên mới: AutoBrand_TC008_1793100000_EDITED` **chứa nguyên** tên cũ `AutoBrand_TC008_1793100000`. Tìm kiếm khớp một phần (REQ-BRAND-10) nên bước 6 *"Tìm theo tên cũ → Không có dòng nào"* không bao giờ đạt — run_1793105365 FAIL vì TC, không vì hệ thống. Expected bước 4 còn `⚠️ chưa có evidence câu chữ` | Test Data: `Tên mới: AutoBrandRenamed_TC008_1793100000` (không chứa tên cũ). Expected 4: `Hiện thông báo xanh Brand has been updated successfully`. Expected 6 giữ nguyên. Gỡ `@NeedsVerify`, `@AssumptionBased` giữ |
| AEC_BRAND_TC_034 | 11/12 (Expected 1) | 🟢 → cập nhật | Biến thể `c` *"Bấm vùng tối bên ngoài hộp"* kỳ vọng hộp đóng, nhưng hộp thư viện được dựng với nền tĩnh (`backdrop: static`) — **chủ đích của thiết kế**, không phải lỗi. Biến thể `b` ESC: hộp khai báo cho phép đóng bằng bàn phím (`keyboard: true`) nhưng không đóng được vì hộp không nhận focus → **lỗi thật**, Expected đúng | Bỏ biến thể `c`, ghi lý do ngay trong Expected: *"Bỏ biến thể `c` (27-10-2026): hộp thư viện cố ý không đóng khi bấm ra ngoài — run_1793105365"*. Giữ `a`, `b`; bỏ `⚠️ chưa có evidence` ở `b`. Tag: gỡ `@NeedsVerify`, thêm `@KnownIssue` (biến thể `b` đang FAIL) |
| AEC_BRAND_TC_041 | 11/12 (Expected 1) | 🟢 → cập nhật | Biến thể `b` `×` đã PASS — ghi chú `⚠️ chưa có evidence` đã cũ. Biến thể `c` ESC: hộp xác nhận cũng khai báo `keyboard: true` nhưng không nhận focus nên ESC không đóng — cùng gốc với TC_034-b, **lỗi thật**, Expected đúng | Giữ cả 3 biến thể, bỏ hai ghi chú `⚠️`. Tag: gỡ `@NeedsVerify`, thêm `@KnownIssue` |

> TC_034-b và TC_041-c nay là **lỗi hệ thống** (trợ năng bàn phím), không còn là "nghi TC viết sai" như execution report ghi — chuyển sang nhóm báo bug.

## Dọn `@NeedsVerify` theo run_1793105365

Execution report đã xác nhận trên UI → gỡ tag và thay ghi chú `⚠️` bằng điều quan sát được:

| TC | Ghi chú cũ | Sửa thành | Gỡ `@NeedsVerify`? |
|---|---|---|---|
| 006 | `Hiện thông báo lưu thành công (⚠️ chưa có evidence câu chữ — AMB-BRAND-07)` | `Hiện thông báo xanh Brand has been inserted successfully` | ✅ |
| 007 | Bước 6 `(⚠️ chưa có evidence)` · bước 9 `Hiện thông báo lưu thành công` | Bước 6 bỏ ghi chú; bước 9 `Brand has been inserted successfully` | ✅ |
| 008 | (xem bảng trên) | | ✅ |
| 009 | `hiện thông báo xóa thành công (⚠️ chưa có evidence câu chữ)` | `hiện thông báo xanh Brand has been deleted successfully` | ✅ |
| 013 | — (Expected đã cụ thể) | Không đổi chữ | ✅ |
| 019 | `Có hay không dòng "không có dữ liệu": ⚠️ chưa có evidence (AMB-BRAND-08)` | `bảng chỉ còn hàng tiêu đề, không có dòng chữ "không có dữ liệu"` | ✅ |
| 028 | `(⚠️ giả định — chưa có evidence)` | Bỏ ghi chú | ✅ |
| 035 | `⚠️ Hành vi chọn 1 ảnh là giả định (AMB-BRAND-12) — chưa có evidence` | Bỏ ghi chú | ✅ |
| 038 | — | Không đổi chữ | ✅ |
| 039 | `b` *"Slug được sinh lại dạng `<tên brand>-<5 ký tự chữ số>`"* | `<tên brand đã bỏ ký tự đặc biệt như _>-<5 ký tự chữ số>` | ✅ |
| 042 | — | Không đổi chữ | ✅ |
| 045 | `(lần lưu sau thắng — ⚠️ giả định, chưa có evidence)` | `(lần lưu sau thắng)` | ✅ |
| 049 | `Cột bảng bị ẩn ở màn hẹp phải có cách mở xem (⚠️ chưa có evidence)` | `ở 768 px cả 4 cột vẫn hiện` | ✅ |
| 050 | `(ô Logo, ⚠️ chưa biết có nhận focus không)` | `Name → Meta Title → Meta description → Save (ô Logo không nhận focus)` | ✅ |

**Giữ `@NeedsVerify`** (run chưa giải quyết được): TC_023, 025, 031 (máy chủ trả lỗi 500 / lưu cắt im lặng — câu thông báo lỗi đúng vẫn chưa biết, AMB-BRAND-04, 05) · TC_036, TC_048 (SKIPPED) · TC_040 (chưa chốt AMB-BRAND-06).

Tổng `@NeedsVerify`: 22 → **6**.

## Ngoài phạm vi lần sửa này — ghi để xử lý sau

- TC_002 Expected mục `5` *"Slug dạng `<tên brand>-<5 ký tự>`"*: thêm *"đã bỏ ký tự đặc biệt"* (run ghi nhận khi PASS)
- TC_014, TC_015 `@KnownIssue`: không còn tái hiện ngày 27-10-2026 → cần cập nhật AMB-BRAND-01 qua `/update-requirements-from-ticket` rồi mới gỡ tag
- TC_024 Test Data giả định giới hạn 255 ký tự; giới hạn quan sát được là 50 (AMB-BRAND-04) — chờ PO chốt
- Requirements: cập nhật AMB-BRAND-07 (câu thông báo), AMB-BRAND-08, AMB-BRAND-12 đã có quan sát

## Đối soát loại kiểm thử (4 vòng)

Không chấm lại — phạm vi FIX chỉ sửa nội dung 3 TC và ghi chú evidence, không thêm/bớt nhánh. Thay đổi duy nhất: V2 `UI Behavior` TC_034 giảm từ 3 xuống 2 biến thể (bỏ biến thể do thiết kế). Kết quả 4 vòng của index giữ nguyên.

## Coverage Gaps (TC còn thiếu)

Không phát sinh gap mới từ lần review này.

## TC trùng lặp — đề xuất merge

Không có.

## Kết luận & Khuyến nghị

1. Sửa TC_008 (Test Data) — TC sai thiết kế, gây FAIL giả
2. TC_034 bỏ biến thể `c`; TC_034-b, TC_041-c gắn `@KnownIssue` và đưa vào nhóm báo bug trợ năng (`/create-bug-report`)
3. Gỡ `@NeedsVerify` cho 14 TC, cập nhật ghi chú evidence; đồng bộ index (Bảng Đối Soát Evidence, Vùng chưa có evidence, Đối soát cột Automation, Nhật ký)

## Kết quả áp dụng (Mode FIX)

- 27-10-2026: user duyệt toàn bộ đề xuất. Đã sửa **tại chỗ** đúng 16 dòng TC: 006, 007, 008, 009, 013, 019, 028, 034, 035, 038, 039, 041, 042, 045, 049, 050 — đối chiếu bằng diff với bản lưu trước khi sửa, không dòng nào khác đổi. TC ID giữ nguyên, 50 dòng vẫn đủ 13 cột.
- Thêm: TC_035 và TC_045 gỡ luôn `@AssumptionBased` (giả định AMB-BRAND-12 và "lần lưu sau thắng" đã được xác nhận).
- Đã đồng bộ index (độ hạt, Coverage REQ-15/20, Bảng Đối Soát Evidence, Vùng chưa có evidence, Đối soát cột Automation, Quality Gate, Nhật ký) và danh mục `docs/testcases/README.md`.
- Không có mốc git — bản trước khi sửa lưu ngoài dự án (scratchpad của phiên, thư mục `backup_before_review_fix_20261027/`).

## Đính chính 28-10-2026 — TC_019

- Đề xuất cho TC_019 ở bảng *Dọn `@NeedsVerify`* (*"bảng chỉ còn hàng tiêu đề, không có dòng chữ 'không có dữ liệu'"*) **sai**: dựa vào ghi nhận của run_1793105365, khi đó đọc HTML máy chủ lúc browser pane bị ẩn nên không thấy dòng `Nothing found` do trình duyệt dựng.
- [run_1793109740](../../../executions/brand/web/run_1793109740/execution_report.md) quan sát trên bảng hiển thị thật: tìm không khớp → bảng hiện dòng `Nothing found` kèm biểu tượng mặt buồn.
- Đã sửa Expected TC_019 thành *"bảng chỉ còn hàng tiêu đề và một dòng `Nothing found` kèm biểu tượng mặt buồn"*. Tag giữ nguyên (`@Regression @AssumptionBased`).
