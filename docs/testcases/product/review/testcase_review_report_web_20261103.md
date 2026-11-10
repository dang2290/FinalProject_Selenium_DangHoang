# Báo Cáo Review Test Cases — Manage Product (web)

## Tổng quan

- **Nguồn:** [part_01](../web/parts/part_01_web_danh_sach.md) · [part_02](../web/parts/part_02_web_tao_gia_bien_the.md) · [part_03](../web/parts/part_03_web_sua_digital_bulk.md) · index [../TEST_CASES_PRODUCT_SUMMARY.md](../TEST_CASES_PRODUCT_SUMMARY.md)
- **Mode:** REVIEW — phạm vi **có trọng tâm**, cùng cách làm với Attribute và Colors: 8 TC `@NeedsVerify` (035, 047, 052, 063, 064, 072, 073, 074), FAIL ngoài dự kiến (TC_025) và TC_067 bị BLOCKED do dữ liệu bị người khác xoá. Không chấm lại toàn bộ 75 TC — bộ TC vừa qua Quality Gate 11/11 lúc sinh (02-11-2026). 13 TC `@KnownIssue` FAIL đúng dự kiến, không cần xét lại
- **Căn cứ đối chiếu:** [run_1793607938](../../../executions/product/web/run_1793607938/execution_report.md) · [requirements_product_web.md](../../../requirements/product/web/requirements_product_web.md) · [REQUIREMENTS_PRODUCT_SUMMARY.md](../../../requirements/product/REQUIREMENTS_PRODUCT_SUMMARY.md)
- **Số TC review:** 10 · **Kết quả:** 🟢 8 · 🟡 2 · 🔴 0 · **Điểm trung bình:** 10,7/12
- **Loại trừ theo requirements:** role khác admin · tải tệp thật lên Bulk Import (⏭️ Q14) · trình duyệt khác Chromium — không chấm, không đề xuất
- **Lưu ý:** thư mục dự án **không phải repo git** → không có mốc git; nếu sửa (mode FIX), bản trước khi sửa sẽ sao lưu ra thư mục tạm của phiên

## Chi tiết từng TC

| TC ID | Điểm | Xếp loại | Vấn đề chính | Đề xuất sửa |
|---|---|---|---|---|
| AEC_PROD_TC_052 | 9/12 | 🟡 | Tiêu chí 4: Test Data *"như TC_051"* = *"2 giá trị đầu của `Metal`"*. Thuộc tính `Metal` hiện có **hai giá trị cùng tên `Test`**: lấy 2 giá trị đầu (`Indigo_EDIT`, `Test`) thì trang sửa hiện **6 dòng trùng** (4078). Lấy 2 giá trị khác tên (`Indigo_EDIT`, `White`) thì đúng 4 dòng (4080). Kết quả phụ thuộc dữ liệu của module khác | Test Data: *"2 màu đầu danh sách · 2 giá trị **khác tên nhau** của `Metal` (ghi lại tên đã chọn khi chạy)"*. Gỡ `@NeedsVerify`, bỏ ⚠️ ở bước 3. Hiện tượng 6 dòng trùng đưa sang Coverage Gap #2 |
| AEC_PROD_TC_035 | 9/12 | 🟡 | Tiêu chí 3: PC *"có sẵn ảnh, tệp PDF test trong thư viện Uploaded Files"* không dựng được — thư viện **không có tệp PDF** (`No files found`), nên `c` BLOCKED. Tiêu chí 2: *"Giá trị đã chọn còn nguyên"* chung chung. `a` (ảnh 3987) và `b` (Youtube) đã chạy đạt | PC: thêm *"Nếu thư viện chưa có PDF, tải 1 tệp PDF test lên `Uploaded Files` trước, xoá sau TC"*. Expected: `a` *"ô Thumbnail hiện ảnh đã chọn"* · `b` *"Video Provider `Youtube`, Video Link đúng link đã nhập"* · `c` *"ô PDF Specification hiện tên tệp đã chọn"*. Gỡ ⚠️ cho `a`, `b`; giữ `@NeedsVerify` tới khi `c` chạy |
| AEC_PROD_TC_025 | 10/12 | 🟢 | Expected *"đủ 255 ký tự"* bám REQ-PROD-19. Chạy thật báo thành công nhưng chỉ lưu **200** ký tự — **TC đúng, hệ thống lệch** (giống Colors TC_025) | **Giữ Expected.** Thêm tag `@KnownIssue` và dòng *"Hiện tại Name bị cắt còn 200 ký tự, không báo (run_1793607938) — chờ mở AMB"*. Mở AMB mới, cập nhật REQ-PROD-19 |
| AEC_PROD_TC_064 | 10/12 | 🟢 | Chưa chạy được: bấm `Bulk Export` tải tệp về máy, cần người chạy được phép tải. TC không nói điều này | PC: thêm *"Được phép tải tệp về máy"*. Giữ `@NeedsVerify` tới lần chạy đầu |
| AEC_PROD_TC_067 | 10/12 | 🟢 | Bước 3 và `a` bước 4 đạt. `b` bước 4 không kiểm được: sản phẩm 4100 **bị người khác xoá** trong lúc chờ đăng nhập lại. Cách viết TC không sai; vấn đề là giả định môi trường *"không dùng chung"* ở đầu part 01 không còn đúng | Không sửa Steps. Ghi chú `b`: *"Nếu sản phẩm test không còn khi kiểm bước 4, chấm BLOCKED, không chấm PASS"*. Xem thêm Phát hiện #1 |
| AEC_PROD_TC_072 | 11/12 | 🟢 | `a`, `b` đạt. `c` FAIL: Esc không đóng hộp xoá — Expected là chuẩn trợ năng thông dụng, cùng kết luận với Attribute TC_040, Colors TC_048 — **TC đúng, hệ thống lệch** | Giữ Expected. Gỡ `@NeedsVerify`, bỏ câu ⚠️ ở `c`, thêm `@KnownIssue` cho `c` |
| AEC_PROD_TC_047 | 12/12 | 🟢 | Ghi chú `⚠️ chưa có evidence` + `@NeedsVerify` đã cũ — chạy thật đúng Expected (Tax `5`, `Percent`) | Gỡ tag, bỏ ghi chú |
| AEC_PROD_TC_063 | 12/12 | 🟢 | Như trên — không gửi, trình duyệt nhắc chọn tệp | Gỡ tag, bỏ câu *"⚠️ chưa có evidence — suy từ thuộc tính bắt buộc"*. Giữ *"Tải tệp thật ⏭️ theo Q14"* |
| AEC_PROD_TC_073 | 12/12 | 🟢 | Như trên — 1920 và 1366 đều đạt. Ở 1366, cột `Added By` và 3 cột công tắc gập vào nút `+`; Expected `b` không đòi đủ cột nên vẫn đạt | Gỡ tag, bỏ ⚠️ ở `b` |
| AEC_PROD_TC_074 | 12/12 | 🟢 | Như trên — trang `/product/<slug>` hiện tên, giá `$10.00` | Gỡ tag, bỏ ⚠️ |

> ✅ **Đã áp dụng 03-11-2026** (user duyệt toàn bộ): TC_052 đổi Test Data, TC_035 thêm PC tải PDF và Expected từng biến thể, TC_064 thêm PC, TC_067 thêm luật chấm BLOCKED, TC_025 và TC_072 thêm `@KnownIssue`, gỡ `@NeedsVerify` 6 TC; index và danh mục đã đồng bộ. Phát hiện #1: user xác nhận **dùng chung** 03-11-2026, đã sửa dòng Môi trường của mọi module.

## Phát hiện thêm ngoài danh sách (đề xuất, chưa sửa)

| # | Chỗ | Vấn đề | Đề xuất |
|---|---|---|---|
| 1 | Đầu part 01 (dòng `Môi trường`) · danh mục `docs/requirements/README.md` | Ghi *"không dùng chung"*. Trong lần chạy có người khác tạo `DL_AutoProduct_…` (id 4104–4106) và xoá sản phẩm 4100 của lần chạy | **Cần user xác nhận** môi trường có dùng chung không. Nếu có: sửa dòng `Môi trường`, bật quy tắc dọn ngay sau TC, chạy TC có bước chờ (TC_067) liền mạch |
| 2 | AEC_PROD_TC_010 `a` | Lần này không quan sát được hộp đóng lại do pane ẩn — giới hạn lúc chạy, không phải lỗi cách viết | Không sửa |
| 3 | AEC_PROD_TC_056 | Slug bản sao thêm hậu tố 5 ký tự (`-QMQiX`); TC_034 trùng tên thêm `-2`. Expected chỉ đòi *"khác bản gốc"* nên vẫn đúng | Không sửa; ghi hai dạng hậu tố vào requirements khi chốt AMB-PROD-15 |

## Đối soát loại kiểm thử (4 vòng)

Giữ nguyên bảng 4 vòng ở index. Kết quả chạy làm thay đổi 2 điểm:

| Vòng | Nhánh | Trạng thái | Ghi chú |
|---|---|---|---|
| 2 | Boundary Value Analysis | 🟡 | Name có ngưỡng lưu thật ở 200 ký tự, bộ TC chỉ phủ 255 · 256 |
| 2 | Business Rule (biến thể) | 🟡 | Chưa có TC cho thuộc tính có giá trị trùng tên — sinh ra dòng biến thể trùng ở trang sửa |

## Coverage Gaps

| # | Kịch bản thiếu | Vòng / Nhánh | Priority đề xuất |
|---|---|---|---|
| 1 | Name 200 ký tự lưu đủ · 201 ký tự (biên thật) | V2 · BVA | Medium — sinh sau khi PO chốt giới hạn Name, qua `/update-testcases-from-impact` |
| 2 | Chọn giá trị trùng tên của một thuộc tính → bảng biến thể ở trang sửa không có dòng trùng | V2 · Business Rule | Medium — cần mở AMB trước (nguyên nhân có thể ở module Attribute cho phép giá trị trùng tên) |

## TC trùng lặp

Không phát hiện trong phạm vi review.

## Kết luận & Khuyến nghị

1. **Sửa TC_052** (Test Data giá trị khác tên) và **TC_035** (PC tải PDF, Expected cụ thể từng biến thể).
2. **TC_025 thêm `@KnownIssue`**; **TC_072 `c` thêm `@KnownIssue`**; **gỡ `@NeedsVerify`** cho TC_047, 052, 063, 072, 073, 074.
3. **Bổ sung PC** cho TC_064 (được tải tệp) và ghi chú BLOCKED cho TC_067 `b`.
4. **TC_025 là lỗi thật mới** — báo bug được; TC_072 `c` gộp bug Esc chung 5 module.
5. **Cập nhật requirements** trước khi mở rộng TC: REQ-PROD-19 (ngưỡng 200), AMB mới cho Name và cho giá trị thuộc tính trùng tên, xác nhận lại môi trường dùng chung.
