# Test Cases — Product Reviews (tổng 19 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_REVIEW_SUMMARY.md](../../requirements/review/REQUIREMENTS_REVIEW_SUMMARY.md) · web: [requirements_review_web.md](../../requirements/review/web/requirements_review_web.md) |
| Mode · độ hạt | FULL RBT · **GỘP** — 19 TC · 28 ca kiểm (8 TC có bảng biến thể, 17 biến thể) |
| Dải TC ID đã dùng | `AEC_REVIEW_TC_001` → `AEC_REVIEW_TC_019` — mã kế tiếp `AEC_REVIEW_TC_020` |
| Phạm vi REQ | `REQ-REVIEW-01` → `REQ-REVIEW-15` (15/15) |
| Mức rủi ro · độ sâu | `Trung bình` → **Tiêu chuẩn** — xem đầu file web. **Nâng lên Cao khi:** có dữ liệu đánh giá thật · có tài khoản role khác |
| Giả định đang áp | Q1–Q6 (≈ AMB-REVIEW-01 → 05) — user chấp nhận toàn bộ 03-11-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Môi trường | **Dùng chung** (user xác nhận 03-11-2026) — chỉ bật/tắt `Published` của đánh giá do test tạo, trả về trạng thái ban đầu (PC-3) |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/test_cases_review_web.md](web/test_cases_review_web.md) | V1 Smoke · V2 Functional · V3 Technical · V4 Non-functional | 19 | 001–019 | REQ-REVIEW-01 → 15 |

## Bảng Đối Soát Coverage

| REQ | Số TC | TC | Ghi chú |
|---|---|---|---|
| REQ-REVIEW-01 | 1 | 001 | ✅ |
| REQ-REVIEW-02 | 1 | 002 | ✅ |
| REQ-REVIEW-03 | 1 | 003 | ✅ (SKIPPED khi đã có đánh giá) |
| REQ-REVIEW-04 | 1 | 017 | ✅ 2 biến thể |
| REQ-REVIEW-05 | 2 | 014, 016 | ✅ |
| REQ-REVIEW-06 | 2 | 009, 019 | 🟡 `@NeedsVerify` — chờ dữ liệu |
| REQ-REVIEW-07 | 2 | 004, 018 | ✅ |
| REQ-REVIEW-08 | 3 | 005, 016, 018 | ✅ |
| REQ-REVIEW-09 | 1 | 006 | ✅ |
| REQ-REVIEW-10 | 1 | 007 | ✅ `@KnownIssue` |
| REQ-REVIEW-11 | 1 | 008 | 🟡 `@NeedsVerify` — chờ dữ liệu |
| REQ-REVIEW-12 | 2 | 010, 015 | ✅ `@KnownIssue` ở 010 |
| REQ-REVIEW-13 | 1 | 011 | 🟡 `@NeedsVerify` — chờ dữ liệu |
| REQ-REVIEW-14 | 1 | 012 | 🟡 `@NeedsVerify` — chờ dữ liệu |
| REQ-REVIEW-15 | 1 | 013 | ✅ |

15/15 REQ có TC. Chiều ngược (6b): TC_016 (REQ-05, 08) và TC_018 (REQ-07, 08) — mỗi REQ đều có TC khác chống lưng (014, 005, 004) ✅.

## Bảng Đối Soát Evidence

| Ảnh | Trạng thái | TC dựa vào |
|---|---|---|
| [review_01_danh_sach_rong.jpg](../../requirements/review/web/evidence/review_01_danh_sach_rong.jpg) | ✅ Đủ — 7 cột, `Nothing found` | 001, 002, 003 |
| [review_02_loc_rating_mo.jpg](../../requirements/review/web/evidence/review_02_loc_rating_mo.jpg) | ✅ Đủ — ô lọc mở, 3 lựa chọn | 004, 005, 007 |
| (không có ảnh — đọc DOM / nguyên văn khi recon) | Thanh địa chỉ khi lọc · trang lỗi 500 · chuyển về `/login` · cột thu gọn ở bề ngang 800 · trang sản phẩm cửa hàng | 005, 010, 013, 014, 017 |

### Vùng chưa có evidence — TC gắn `@NeedsVerify`

| TC | Vùng chưa quan sát | Đề xuất |
|---|---|---|
| 008, 009, 011, 012, 019 | Dòng đánh giá, thứ tự sao, công tắc, hiệu lực ở cửa hàng | Cần dữ liệu đánh giá (AMB-REVIEW-02) |

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | 002, 003, 004 (bảng kiểm nhãn, thứ tự cột, mặc định ô lọc) |
| 1 | Điều hướng | ✅ | 001 |
| 2 | Data Table / List | ✅ | 003, 009 |
| 2 | Lọc / sắp xếp | ✅ | 005 (2 biến thể), 006, 007 (2), 008 (2) |
| 2 | Validation | ✅ | 010 (3 biến thể) — tham số `rating`, ô nhập duy nhất của trang |
| 2 | Required | ➖ | Trang không có trường bắt buộc |
| 2 | CRUD Lifecycle | ➖ | Trang không tạo, sửa, xoá đánh giá (REQ-REVIEW-15, kiểm ở 013) |
| 2 | Modal / Dialog | ➖ | Trang không có hộp thoại |
| 2 | Status Flow | ✅ | 011 (bật → tắt → bật), 012 (hiệu lực ở cửa hàng) |
| 2 | Notification | ✅ | 011 |
| 2 | Business Rule | ✅ | 013 (2 biến thể) |
| 3 | Permission | ✅ | 014 (2 biến thể), 016. Role khác ⏭️ — chưa có tài khoản; user quyết 03-11-2026 (Q5); rà lại khi có tài khoản (AMB-REVIEW-05) |
| 3 | Security | ✅ | 015 (2 biến thể) |
| 3 | Session | ✅ | 016 |
| 3 | API · Database · Integration · Logging/Audit | ⏭️ | QA không có quyền — chốt 27-10-2026 ở `docs/requirements/README.md`; đội Dev xác minh; rà lại khi QA được cấp quyền |
| 4 | Responsive | ✅ | 017 (2 biến thể) |
| 4 | Accessibility | ✅ | 018 |
| 4 | Localization | ✅ | 019 |
| 4 | Performance | ⏭️ | Không có công cụ đo tải — giống các module trước; đội hạ tầng nếu cần |
| 4 | Compatibility | ⏭️ | Chỉ Chromium — user quyết 02-11-2026; rà lại khi mở rộng trình duyệt |

## Đối soát cột Automation

| Kết quả | Số TC | TC |
|---|---|---|
| Yes | 15 | Còn lại |
| Partial | 4 | 009 · 012 · 017 · 019 |
| No | 0 | — |

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_REVIEW_TC_009 | Web | Partial | 2 · dữ liệu chéo mặt | Cần tài khoản khách test gửi đánh giá ở cửa hàng |
| AEC_REVIEW_TC_012 | Web | Partial | 2 · mặt cửa hàng | Cửa hàng có popup quảng cáo cần đóng; cần đánh giá test |
| AEC_REVIEW_TC_017 | Web | Partial | 2 · Expected cần mắt người | Tự động: cột ẩn/hiện, không cuộn ngang. Kiểm tay: bố cục |
| AEC_REVIEW_TC_019 | Web | Partial | 2 · dữ liệu chéo mặt | Cần gửi đánh giá ở cửa hàng |
| AEC_REVIEW_TC_008 · 009 · 011 · 012 · 019 | Web | ⏸️ Hoãn | — | `@NeedsVerify` — chạy tay lần đầu để chốt Expected rồi mới automate |

**Lưu ý automation:** công tắc `Published` lưu ngay, không có hộp xác nhận — automation phải trả về trạng thái ban đầu trong teardown. Tham số `rating` sai gây lỗi 500 (TC_010) — không dùng làm bước dọn dẹp.

**Thứ tự ưu tiên automate:** `@Smoke` (TC_001–004) → TC_005, 007, 010, 014–016 → phần còn lại.

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–013 |
| Performance Efficiency | ➖ Không áp dụng | Bảng đang rỗng, không có ngưỡng — đội hạ tầng nếu cần đo tải |
| Compatibility | ➖ Ngoài phạm vi | Chỉ Chromium — user quyết 02-11-2026; liên kết với cửa hàng ở TC_012, 013 |
| Interaction Capability | ✅ Có TC | TC_004, 007 (ô lọc rõ trạng thái) · 018 (bàn phím) |
| Reliability | ✅ Có TC | TC_010 (tham số sai) · 016 (hết phiên) |
| Security | ✅ Có TC | TC_014, 015, 016 |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn, không kiểm bằng manual TC |
| Flexibility | ✅ Có TC | TC_017 (1920 · 800) · 019 (Unicode, emoji) |
| Safety | ✅ Có TC | TC_011, 012 (trả công tắc về trạng thái ban đầu, PC-3) |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 3 biến thể/TC; TC thiết kế để FAIL (007, 010) tách riêng, mỗi TC một lỗi |
| 1 | Unique TC ID | ✅ 19 ID, không trùng, đúng mẫu `AEC_REVIEW_TC_<3 số>` |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected đánh số theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Địa chỉ, lựa chọn, nội dung đánh giá có giá trị cụ thể; `<ts>` có ví dụ `1793700000` |
| 4 | Field validation coverage | ✅ 1 ô chọn (Dropdown): đủ mục — mặc định (004), từng lựa chọn (005), chọn lại mặc định (006), giữ giá trị (007), giá trị ngoài danh sách (010, 015), bàn phím (018) |
| 5 | Automation metadata | ✅ 19/19 có `Automation` · `Auto Type` · tag; 4 Partial có lý do |
| 6 | Requirement coverage | ✅ 15/15 REQ; 6b đạt |
| 7 | Evidence-verified | ✅ Đã mở 2/2 ảnh; vùng chưa quan sát gắn `@NeedsVerify` (7 TC lúc sinh; còn 5 TC sau review 03-11-2026 — đều chờ dữ liệu) |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không có thuật ngữ DOM ở phần TC chính. `Error code: 500` là chữ hiển thị trên trang |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴; `UI cơ bản` · `Validation` · `Permission` đều ✅ |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 03-11-2026 | `/generate-testcases-manual-rbt review` (FULL RBT) | AEC_REVIEW_TC_001 → 019 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-REVIEW-01 → 15, độ hạt GỘP, rủi ro Trung bình, giả định Q1–Q6 |
| 03-11-2026 | `/review-testcases review` mode FIX ([báo cáo](review/testcase_review_report_web_20261103.md)) theo [run_1793676756](../../executions/review/web/run_1793676756/execution_report.md) | 006, 010, 013, 018 | 🟡 Sửa | TC_013 thêm bước đóng popup quảng cáo trước khi bấm tab `reviews`. TC_010 cập nhật hiện trạng (`c` đạt). Gỡ `@NeedsVerify` TC_006, 018. Không có mốc git (thư mục chưa là repo) — bản trước khi sửa sao lưu ở thư mục tạm của phiên `backup_review_tc_20261103` |
