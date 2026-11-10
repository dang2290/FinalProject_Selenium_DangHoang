# Báo Cáo Review Test Cases — Product Reviews (web)

## Tổng quan

- **Nguồn:** [../web/test_cases_review_web.md](../web/test_cases_review_web.md) · index [../TEST_CASES_REVIEW_SUMMARY.md](../TEST_CASES_REVIEW_SUMMARY.md)
- **Mode:** REVIEW — phạm vi **có trọng tâm**, cùng cách làm với các module trước: TC `@NeedsVerify` đã có kết quả chạy (006, 018), TC có biến thể hoặc bước lệch so với lúc chạy thật (010, 013, 014). Không chấm lại toàn bộ 19 TC — bộ TC vừa qua Quality Gate 11/11 lúc sinh (03-11-2026). 5 TC BLOCKED vì thiếu dữ liệu giữ nguyên `@NeedsVerify`, không xét lại
- **Căn cứ đối chiếu:** [run_1793676756](../../../executions/review/web/run_1793676756/execution_report.md) · [requirements_review_web.md](../../../requirements/review/web/requirements_review_web.md) · [REQUIREMENTS_REVIEW_SUMMARY.md](../../../requirements/review/REQUIREMENTS_REVIEW_SUMMARY.md)
- **Số TC review:** 5 · **Kết quả:** 🟢 4 · 🟡 1 · 🔴 0 · **Điểm trung bình:** 11,0/12
- **Loại trừ theo requirements:** role khác admin (⏭️ Q5 · AMB-REVIEW-05) · API, CSDL, tích hợp, nhật ký (⏭️ năng lực QA) · trình duyệt khác Chromium — không chấm, không đề xuất
- **Lưu ý:** thư mục dự án **không phải repo git** → không có mốc git; nếu sửa (mode FIX), bản trước khi sửa sẽ sao lưu ra thư mục tạm của phiên

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_REVIEW_TC_013 | 9/12 | 🟡 | Tiêu chí 1: bước 2 *"bấm tab `reviews`"* — trên trang sản phẩm, **popup quảng cáo** của cửa hàng che tab, tester không bấm được nếu không đóng. Bước đóng popup không có trong TC | Bước 2 tách thành: *"2. Mở trang sản phẩm theo Test Data; nếu có popup quảng cáo, đóng bằng nút X (không bấm đăng ký, không bấm cookie)"* · *"3. Bấm tab `reviews`"*. Expected `b` chuyển sang bước 3 |
| AEC_REVIEW_TC_006 | 11/12 | 🟢 | Chạy thật đúng Expected → `@NeedsVerify` và ghi chú `⚠️ chưa có evidence` đã cũ. Lúc chạy còn thấy: khi **đang lọc**, chọn lại `Filter by Rating` cũng không bỏ được bộ lọc — TC chỉ phát biểu cho trang mặc định nên không sai | Gỡ tag, bỏ ⚠️. Hành vi "không bỏ được bộ lọc" đưa sang Coverage Gap #1 |
| AEC_REVIEW_TC_010 | 11/12 | 🟢 | `c` (`?rating=` rỗng) đã chạy: hiện danh sách mặc định — câu *"`c` ⚠️ chưa thao tác"* đã cũ | Đổi thành *"Hiện tại `a`, `b` ra trang lỗi … (AMB-REVIEW-03); `c` đạt"* |
| AEC_REVIEW_TC_014 | 11/12 | 🟢 | Tiền đề *"Cửa sổ ẩn danh"* đúng ý. Lần chạy thay bằng yêu cầu không kèm phiên đăng nhập — cùng điều kiện, không phải lỗi TC | Không sửa |
| AEC_REVIEW_TC_018 | 12/12 | 🟢 | Chạy thật đúng Expected (viền cam khi focus, Enter mở, mũi tên + Enter lọc) | Gỡ `@NeedsVerify`, bỏ ⚠️ |

> ✅ **Đã áp dụng 03-11-2026** (user duyệt toàn bộ): TC_013 thêm bước đóng popup, TC_010 cập nhật hiện trạng, gỡ `@NeedsVerify` TC_006, 018; index và danh mục đã đồng bộ. Coverage Gap #1 chưa xử lý — cần mở AMB trước.

## Phát hiện thêm ngoài danh sách (đề xuất, chưa sửa)

| # | Chỗ | Vấn đề | Đề xuất |
|---|---|---|---|
| 1 | AEC_REVIEW_TC_015 | Expected *"không bật alert, không lộ thông tin kỹ thuật"* đạt, nhưng trang vẫn ra lỗi 500 (cùng gốc TC_010). Hai TC kiểm hai khía cạnh khác nhau, không trùng | Không sửa |
| 2 | 5 TC BLOCKED (008, 009, 011, 012, 019) | Đúng thiết kế — PC-2 đã nói chưa có dữ liệu thì chấm BLOCKED | Giữ nguyên tới khi có dữ liệu (AMB-REVIEW-02) |

## Đối soát loại kiểm thử (4 vòng)

Giữ nguyên bảng 4 vòng ở index. Kết quả chạy làm thay đổi 1 điểm:

| Vòng | Nhánh | Trạng thái | Ghi chú |
|---|---|---|---|
| 2 | Lọc / sắp xếp | 🟡 | Chưa có TC cho việc **bỏ bộ lọc** khi đang lọc — chọn lại `Filter by Rating` không có tác dụng, chỉ còn cách mở lại trang |

## Coverage Gaps

| # | Kịch bản thiếu | Vòng / Nhánh | Priority đề xuất |
|---|---|---|---|
| 1 | Đang lọc `Rating (High > Low)` → chọn lại `Filter by Rating` → trở về danh sách mặc định | V2 · Lọc | Medium — cần REQ trước: mở AMB mới (*"có cần cách bỏ lọc trên giao diện không?"*), rồi sinh TC qua `/update-testcases-from-impact` |

## TC trùng lặp

Không phát hiện trong phạm vi review.

## Kết luận & Khuyến nghị

1. **Sửa TC_013** (thêm bước đóng popup quảng cáo).
2. **Gỡ `@NeedsVerify`** cho TC_006, 018; **cập nhật dòng hiện trạng** của TC_010 (`c` đạt).
3. **TC_007, TC_010 là lỗi thật** — báo bug được.
4. **Cập nhật requirements**: mở AMB về cách bỏ bộ lọc; giữ AMB-REVIEW-02 là việc chặn 5 TC.
