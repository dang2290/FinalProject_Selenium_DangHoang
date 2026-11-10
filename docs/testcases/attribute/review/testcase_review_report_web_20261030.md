# Báo Cáo Review Test Cases — Attribute (web)

## Tổng quan

- **Nguồn:** [../web/test_cases_attribute_web.md](../web/test_cases_attribute_web.md) · index [../TEST_CASES_ATTRIBUTE_SUMMARY.md](../TEST_CASES_ATTRIBUTE_SUMMARY.md)
- **Mode:** FIX — phạm vi **có trọng tâm** theo yêu cầu user 30-10-2026: TC FAIL cần đối chiếu (TC_025, 040, 041, 042) + 5 TC `@NeedsVerify` đã có kết quả chạy (TC_019, 030, 031, 040, 041). Không chấm lại toàn bộ 42 TC — bộ TC vừa qua Quality Gate 11/11 lúc sinh (30-10-2026)
- **Căn cứ đối chiếu:** [run_1793348598](../../../executions/attribute/web/run_1793348598/execution_report.md) · [requirements_attribute_web.md](../../../requirements/attribute/web/requirements_attribute_web.md) · [REQUIREMENTS_ATTRIBUTE_SUMMARY.md](../../../requirements/attribute/REQUIREMENTS_ATTRIBUTE_SUMMARY.md)
- **Số TC review:** 7 · **Kết quả:** 🟢 5 · 🟡 2 · 🔴 0 · **Điểm trung bình:** 10,9/12
- **Loại trừ theo requirements:** phân quyền role khác (⏭️ AMB-ATTR-10) · trình duyệt khác Chromium (⏭️ user quyết 30-10-2026) — không chấm, không đề xuất
- **Lưu ý:** thư mục dự án **không phải repo git** → không có mốc git cho bản trước khi sửa; bản trước khi sửa sẽ sao lưu ra thư mục tạm của phiên (giống lần review Category)

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_ATTR_TC_030 | 9/12 | 🟡 | Tiêu chí 2: Expected bước 2 và 4 ghi hai kết quả *"hoặc chặn xoá… hoặc xoá thành công"* — chạy với hệ thống nào cũng PASS, không phát hiện được gì. Chạy thật: **không chặn**, xoá thành công, ô `Filtering Attributes` của danh mục về `Nothing selected` | Viết lại theo hành vi đã quan sát: *"2. Hiện `Attribute has been deleted successfully`; dòng thuộc tính test không còn · 3. Trang sửa danh mục mở bình thường, tiêu đề `Category Information` · 4. Ô `Filtering Attributes` hiện `Nothing selected`"*. Gỡ `@NeedsVerify` + ghi chú `⚠️ chưa có evidence`. **Giữ `@AssumptionBased`** — AMB-ATTR-07 chưa được PO chốt; PO chọn "phải chặn xoá" thì sửa lại Expected |
| AEC_ATTR_TC_042 | 9/12 | 🟡 | Tiêu chí 2: ngưỡng *"≤ 3 giây"* là giả định của bộ TC, requirements không có cam kết thời gian tải (REQ-ATTR-03 chỉ nói "không phân trang"). Chạy thật 3,99 / 4,00 s → FAIL nhưng **không xác định được TC sai hay hệ thống sai** | **Không sửa Expected** khi chưa có ngưỡng chính thức. Đề nghị mở câu hỏi cho PO (gộp vào AMB-ATTR-12 — 600 dòng một trang) qua `/update-requirements-from-ticket` hoặc khi PO trả lời. Kết quả FAIL lần này không báo bug |
| AEC_ATTR_TC_019 | 12/12 | 🟢 | Tag `@NeedsVerify` + ghi chú *"⚠️ chưa có evidence — hành vi suy từ thuộc tính bắt buộc của ô, chưa bấm thử"* đã cũ — chạy thật đúng Expected | Gỡ `@NeedsVerify`, bỏ ghi chú ⚠️ ở Expected bước 4 |
| AEC_ATTR_TC_031 | 12/12 | 🟢 | Biến thể `b` còn ghi *"⚠️ chưa có evidence — chưa mở ô này lúc khảo sát"* — đã mở và thấy đúng | Gỡ `@NeedsVerify`, bỏ ghi chú ⚠️ ở `b` |
| AEC_ATTR_TC_040 | 11/12 | 🟢 | Biến thể `b` (nút đang focus có viền) và `c` (Esc đóng hộp) FAIL. Cả hai là chuẩn trợ năng thông dụng (dấu hiệu focus nhìn thấy được; Esc đóng hộp thoại), không mâu thuẫn REQ-ATTR-07, 31 — **TC đúng, hệ thống lệch**. `c` cùng hành vi đã gặp ở Brand và Category | Giữ nguyên Expected. Gỡ `@NeedsVerify`, bỏ ghi chú ⚠️ ở `c` → kết quả FAIL là lỗi thật (Low), báo bug được |
| AEC_ATTR_TC_041 | 11/12 | 🟢 | Biến thể `b` FAIL rõ ràng (cột nút bị cột phải phủ ở 1366 px). Tiêu chí 3: biến thể `a` (*"không có thanh cuộn ngang"*) chỉ đúng khi trên môi trường không còn giá trị dài của TC_025, TC_038 — Pre-Condition chưa nêu | Gỡ `@NeedsVerify`, bỏ ghi chú ⚠️ ở `b`. Pre-Condition thêm: *"Giá trị dài của TC_025, TC_038 đã dọn theo PC-3"* |
| AEC_ATTR_TC_025 | 12/12 | 🟢 | Biến thể `b` FAIL: giá trị 256 ký tự lưu còn 255. Expected khớp REQ-ATTR-26 (*"51 và 256 ký tự lưu **nguyên vẹn**"*) — **TC đúng theo REQ**, nhưng thực tế mâu thuẫn với kết quả khảo sát của chính REQ đó | **Không sửa TC.** Đưa lệch này về requirements: REQ-ATTR-26 và AMB-ATTR-05 cần cập nhật (giới hạn thật nghi là 255, cắt im lặng) qua `/update-requirements-from-ticket`; sau đó mới quyết TC_025b giữ hay đổi |

> ✅ **Đã áp dụng 30-10-2026** (user duyệt toàn bộ): TC_030 viết lại Expected, TC_041 thêm Pre-Condition, gỡ `@NeedsVerify` + ghi chú ⚠️ ở TC_019, 030, 031, 040, 041; index và danh mục đã đồng bộ. TC_025, 042 giữ nguyên.

## Phát hiện thêm ngoài danh sách (đề xuất, chưa sửa)

| TC ID | Vấn đề | Đề xuất |
|---|---|---|
| AEC_ATTR_TC_028 | Lần chạy này cột `Options` nằm ngoài màn hình do giá trị 255 ký tự của TC_025 chưa dọn (người chạy hoãn dọn tới cuối buổi). TC đúng — PC-3 đã yêu cầu dọn **sau mỗi TC** | Không sửa TC. Ghi nhớ khi chạy: dọn giá trị dài ngay sau TC_025, TC_038 |
| AEC_ATTR_TC_005 · 033 · 040a | Thông báo xanh tắt trước khi bảng ~640 dòng tải xong nên khó đọc kịp (cùng nguyên nhân TC_042) | Không sửa — Expected đúng; người chạy cần nhìn ngay khi trang chuyển. Sẽ tự hết nếu trang nhanh hơn |

## Đối soát loại kiểm thử (4 vòng)

Giữ nguyên bảng 4 vòng ở index (Quality Gate 30-10-2026). Kết quả chạy chỉ làm thay đổi 1 điểm:

| Vòng | Nhánh | Trạng thái | Ghi chú |
|---|---|---|---|
| 4 | Performance | 🟡 | TC_042 có, nhưng ngưỡng chưa có căn cứ — chờ PO |

## Coverage Gaps

Không có gap mới. Các hành vi lần chạy mới thấy đều **đã có TC phủ** và đã FAIL đúng chỗ:

- Thuộc tính tên rỗng hiện thành mục trống ở Category và Product → hệ quả của AMB-ATTR-02, đã có TC_015, TC_020
- Giá trị bị cắt ở 255 → TC_025b
- 1366 px không bấm được nút → TC_041b

## TC trùng lặp

Không phát hiện trong phạm vi review.

## Kết luận & Khuyến nghị

1. **Sửa 1 TC viết chưa đo được** (TC_030 — bỏ Expected hai nhánh, ghi hành vi đã quan sát); bổ sung Pre-Condition TC_041.
2. **Gỡ `@NeedsVerify`** cho TC_019, 030, 031, 040, 041 và bỏ các ghi chú `⚠️ chưa có evidence` tương ứng.
3. **TC_040b, 040c, 041b, 025b là lỗi thật** — báo bug được. 040c nên gộp một bug chung Brand · Category · Attribute.
4. **TC_042 chờ PO chốt ngưỡng** — không sửa TC, không báo bug.
5. **Cập nhật requirements** trước khi động tới TC_025: REQ-ATTR-26 / AMB-ATTR-05 (255 ký tự).
