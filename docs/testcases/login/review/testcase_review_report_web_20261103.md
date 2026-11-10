# Báo Cáo Review Test Cases — Đăng nhập · Tài khoản (web)

## Tổng quan

- **Nguồn:** [../web/test_cases_login_web.md](../web/test_cases_login_web.md) · index [../TEST_CASES_LOGIN_SUMMARY.md](../TEST_CASES_LOGIN_SUMMARY.md)
- **Mode:** REVIEW — phạm vi **có trọng tâm**, cùng cách làm với các module trước: TC `@NeedsVerify` đã có kết quả chạy (035, 036, 037, 038), FAIL ngoài dự kiến (037a), TC có bước vướng khi chạy thật (011, 019, 024, 032/010b, 033). Không chấm lại toàn bộ 41 TC — bộ TC vừa qua Quality Gate 11/11 lúc sinh (03-11-2026). 8 TC `@KnownIssue` FAIL đúng dự kiến, 6 TC BLOCKED vì thiếu dữ liệu — không xét lại
- **Căn cứ đối chiếu:** [run_1793688841](../../../executions/login/web/run_1793688841/execution_report.md) · [requirements_login_web.md](../../../requirements/login/web/requirements_login_web.md) · [REQUIREMENTS_LOGIN_SUMMARY.md](../../../requirements/login/REQUIREMENTS_LOGIN_SUMMARY.md)
- **Số TC review:** 10 · **Kết quả:** 🟢 7 · 🟡 3 · 🔴 0 · **Điểm trung bình:** 10,9/12
- **Loại trừ theo requirements:** role seller (⏭️ Q4 · AMB-LOGIN-11) · API, CSDL, tích hợp, nhật ký (⏭️ năng lực QA) · trình duyệt khác Chromium — không chấm, không đề xuất
- **Lưu ý:** thư mục dự án **không phải repo git** → không có mốc git; nếu sửa (mode FIX), bản trước khi sửa sẽ sao lưu ra thư mục tạm của phiên

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_LOGIN_TC_024 | 9/12 | 🟡 | Tiêu chí 1: các trang cửa hàng hiện **popup quảng cáo che liên kết** — lần chạy bấm `Log In` không ăn tới khi đóng popup. PC-6 có nói đóng popup, nhưng bước 1 chỉ ghi *"đóng popup nếu có"* cho trang xuất phát; popup có thể hiện **sau** khi trang tải xong | Bước 1: *"Mở trang xuất phát, **chờ trang tải xong**, đóng popup quảng cáo bằng nút X nếu hiện"*. Áp cùng câu cho TC_003, 004, 005, 021, 022, 025, 026, 030, 040 (đều có PC-6) |
| AEC_LOGIN_TC_032 | 9/12 | 🟡 | Tiêu chí 3: bước 2 thử email admin + mật khẩu sai, **trùng** TC_010b. Chạy cả hai = 2 lần sai với tài khoản admin dùng chung, đi ngược PC-6. Lần chạy đã gộp thành 1 lần | Ghi chú ở cả 2 TC: *"TC_010b và TC_032 bước 2 dùng chung **một** lần thử — chạy TC này thì lấy kết quả của TC kia, không thử lại"* |
| AEC_LOGIN_TC_011 | 9/12 | 🟡 | Tiêu chí 3: TC đòi đóng hẳn trình duyệt rồi mở lại — browser pane không làm được, lần chạy BLOCKED. Không phải lỗi cách viết, nhưng TC không nói môi trường chạy | PC: thêm *"Chạy trên Google Chrome thật (đóng được cả cửa sổ trình duyệt)"*. Giữ `@NeedsVerify` |
| AEC_LOGIN_TC_037 | 10/12 | 🟢 | `a` FAIL: thứ tự Tab đúng nhưng `Remember Me`, `Forgot password ?`, `Login` **không có dấu hiệu nào** khi được chọn bằng Tab — Expected là chuẩn trợ năng thông dụng, **TC đúng, hệ thống lệch** (cùng kết luận với các module trước). `b` PASS | Giữ Expected. Gỡ `@NeedsVerify`, bỏ câu ⚠️, thêm `@KnownIssue` cho `a` kèm *"Hiện tại 3 phần tử cuối không có viền khi được chọn (run_1793688841)"*. Mở AMB mới |
| AEC_LOGIN_TC_033 | 10/12 | 🟢 | Bước 1 đã đủ chứng minh lỗi; bước 2 cần tài khoản khách nên chưa chạy. Expected đúng | Không sửa Steps. Ghi chú: *"Bước 1 đã FAIL thì chấm FAIL, không cần đợi bước 2"* |
| AEC_LOGIN_TC_019 | 11/12 | 🟢 | Bước 3 *"Nhấn F5"* — trong browser pane phím F5 không tải lại trang; lần chạy thay bằng mở lại địa chỉ. Trên trình duyệt thật F5 dùng được | Bước 3: *"Nhấn F5 (hoặc bấm vào thanh địa chỉ rồi Enter)"* |
| AEC_LOGIN_TC_035 | 12/12 | 🟢 | `@NeedsVerify` + ⚠️ đã cũ — chạy thật 2/2 đúng Expected | Gỡ tag, bỏ ghi chú |
| AEC_LOGIN_TC_036 | 12/12 | 🟢 | Như trên — nhấp đúp chỉ ra 1 thông báo | Gỡ tag, bỏ ghi chú |
| AEC_LOGIN_TC_038 | 12/12 | 🟢 | Như trên — 1920 · 1366 · 800 đều đạt cả 2 trang | Gỡ tag, bỏ ghi chú |
| AEC_LOGIN_TC_020 | 12/12 | 🟢 | Chạy gộp với 007, 019 trong một lần đăng nhập — hợp lý, tiết kiệm một lần user phải đăng nhập lại | Ghi chú ở TC_020: *"Có thể chạy cùng TC_007, 019 — dùng bước đăng xuất ở tab 2"* |

> ✅ **Đã áp dụng 03-11-2026** (user duyệt toàn bộ): PC-6 + TC_024 chờ tải xong rồi đóng popup; TC_010b/032 dùng chung một lần thử; TC_011 chạy Chrome thật; ghi chú TC_019, 020, 033; TC_037 thêm `@KnownIssue`; gỡ `@NeedsVerify` TC_035–038; index có mục Bộ chạy đề xuất; danh mục đã đồng bộ. AMB mới cho TC_037a chưa mở — cần cập nhật requirements.

## Phát hiện thêm ngoài danh sách (đề xuất, chưa sửa)

| # | Chỗ | Vấn đề | Đề xuất |
|---|---|---|---|
| 1 | AEC_LOGIN_TC_020 bước 3 | Expected *"không có brand mới"* không kiểm được sau khi đã đăng xuất (phải đăng nhập lại mới tìm brand). `419` nghĩa là yêu cầu bị chặn trước khi xử lý — đủ căn cứ | Không sửa; tester có thể kiểm thêm khi đăng nhập lại |
| 2 | Thứ tự chạy | 9 TC cần phiên đăng nhập; chạy đúng thứ tự (006 → 018 → 014–017 → 020 + 007 + 019) chỉ cần user đăng nhập **một lần** | Ghi thứ tự này vào mục *Bộ chạy đề xuất* của index |

## Đối soát loại kiểm thử (4 vòng)

Giữ nguyên bảng 4 vòng ở index. Kết quả chạy không làm thay đổi trạng thái nhánh nào.

## Coverage Gaps

Không phát hiện trong phạm vi review. Lỗi focus của TC_037a nằm trong REQ-LOGIN-03 đã có.

## TC trùng lặp

TC_010b và TC_032 bước 2 trùng thao tác (cố ý — hai TC kiểm hai khía cạnh khác nhau). Không gộp; chỉ ghi chú dùng chung một lần thử (xem TC_032).

## Kết luận & Khuyến nghị

1. **Sửa TC_024** (chờ tải xong rồi đóng popup — áp cho 10 TC trang cửa hàng), **TC_032 + 010b** (dùng chung một lần thử email admin), **TC_011** (chạy trên Chrome thật).
2. **TC_037a thêm `@KnownIssue`**; **gỡ `@NeedsVerify`** cho TC_035, 036, 037, 038.
3. Ghi chú nhỏ ở TC_019, 020, 033; thêm thứ tự chạy vào index.
4. **TC_033 (bảo mật), TC_019, TC_037a là lỗi thật** — báo bug được. Mở AMB mới cho TC_037a.
