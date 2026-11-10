# Test Cases Web — Product Reviews (`REVIEW`)

> Index: [../TEST_CASES_REVIEW_SUMMARY.md](../TEST_CASES_REVIEW_SUMMARY.md) · Requirements web: [requirements_review_web.md](../../../requirements/review/web/requirements_review_web.md) · Index requirements: [REQUIREMENTS_REVIEW_SUMMARY.md](../../../requirements/review/REQUIREMENTS_REVIEW_SUMMARY.md)

| Thông tin | Nội dung |
|---|---|
| **Mode · độ hạt** | FULL RBT · **GỘP** (bảng biến thể `a`, `b`…; tối đa 6 biến thể/TC) |
| **Mức rủi ro · độ sâu** | Module `Trung bình` → **Tiêu chuẩn**. Căn cứ: trang chủ yếu để xem, không có form nhập; công tắc `Published` đổi ngay thứ khách hàng thấy (RISK-REVIEW-02); tham số lọc gây lỗi 500 (AMB-REVIEW-03). 19 TC — thấp hơn khoảng 20–30 vì module chỉ có 15 REQ và 1 ô chọn.<br>**Nâng lên Cao khi:** có dữ liệu đánh giá thật · có tài khoản role khác |
| **Dải TC ID** | `AEC_REVIEW_TC_001` → `AEC_REVIEW_TC_019` |
| **Môi trường** | `https://cms.anhtester.com` — **dùng chung** (user xác nhận 03-11-2026). **Chỉ** bật/tắt `Published` của đánh giá do test tạo, trả về trạng thái ban đầu ngay sau TC (Q6) |
| **Trình duyệt** | Google Chrome bản mới nhất, desktop 1920×1080, trừ TC ghi kích thước riêng |

**Tiền đề dùng chung (cột Pre-Condition dẫn chiếu):**

| Mã | Nội dung |
|---|---|
| **PC-1** | Đã đăng nhập tài khoản admin (tài khoản lấy từ `.env`, không ghi trong tài liệu) và đang ở trang bất kỳ của khu quản trị |
| **PC-2** | Hệ thống có **ít nhất 2 đánh giá** với số sao khác nhau, trong đó có 1 đánh giá do test tạo (tài khoản khách hàng test gửi ở trang sản phẩm của cửa hàng). Chưa có → TC chấm **BLOCKED** (Q2 · AMB-REVIEW-02) |
| **PC-3** | ⛔ **An toàn dữ liệu:** không bấm công tắc `Published` của đánh giá không do test tạo. Đánh giá test bật/tắt xong phải trả về trạng thái ban đầu |

**Quy ước Test Data:** `<ts>` = Unix timestamp (giây) lúc chạy, ví dụ `1793700000`.

**Tag đặc biệt:** `@KnownIssue` — TC bám kỳ vọng đúng nhưng hệ thống hiện đang lệch (dự kiến FAIL) · `@AssumptionBased` — Expected dựa trên giả định Q1–Q6 user chấp nhận 03-11-2026 · `@NeedsVerify` — chưa có thao tác thật chống lưng cho Expected (phần lớn vì bảng chưa có đánh giá).

## V1 — Smoke

| TC ID | REQ ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data | Automation | Auto Type | Tags |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| AEC_REVIEW_TC_001 | REQ-REVIEW-01 | Review / Điều hướng | Medium | Mở trang Product Reviews từ sidebar | PC-1 | 1. Bấm `Products` ở sidebar<br>2. Bấm `Product Reviews` | 2. Mở `https://cms.anhtester.com/reviews`; tiêu đề trang `Product Reviews`, thẻ bảng cũng tên `Product Reviews`; mục `Product Reviews` trong sidebar được tô đậm | High | — | Yes | UI | @Smoke @CriticalPath |
| AEC_REVIEW_TC_002 | REQ-REVIEW-02 | Review / Danh sách | Medium | Bảng đủ 7 cột đúng thứ tự | PC-1 | 1. Mở trang `Product Reviews`<br>2. Đọc hàng tiêu đề của bảng | 2. Đúng thứ tự `#`, `Product`, `Product Owner`, `Customer`, `Rating`, `Comment`, `Published` | High | Màn 1920×1080 | Yes | UI | @Smoke @UI |
| AEC_REVIEW_TC_003 | REQ-REVIEW-03 | Review / Danh sách | Low | Bảng rỗng hiện Nothing found | PC-1 · hệ thống **chưa có** đánh giá nào | 1. Mở trang `Product Reviews`<br>2. Quan sát thân bảng | 2. Biểu tượng mặt buồn và chữ `Nothing found`; không có thanh phân trang | Medium | — (đã có đánh giá thì chấm SKIPPED, ghi lý do) | Yes | UI | @Smoke @UI |
| AEC_REVIEW_TC_004 | REQ-REVIEW-07 | Review / Lọc | Medium | Ô lọc rating có đúng mặc định và lựa chọn | PC-1 | 1. Mở trang `Product Reviews`<br>2. Đọc ô lọc ở góc phải thẻ bảng<br>3. Bấm mở ô lọc | 2. Ô hiện `Filter by Rating`<br>3. Đúng 3 lựa chọn theo thứ tự: `Filter by Rating` (đang chọn), `Rating (High > Low)`, `Rating (Low > High)` | High | — | Yes | UI | @Smoke @UI |

## V2 — Functional

| TC ID | REQ ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data | Automation | Auto Type | Tags |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| AEC_REVIEW_TC_005 | REQ-REVIEW-08 | Review / Lọc | Medium | Chọn lựa chọn là lọc ngay | PC-1 | 1. Mở trang `Product Reviews`<br>2. Mở ô lọc, chọn lựa chọn theo biến thể<br>3. Quan sát trang và thanh địa chỉ | **Mọi biến thể:** 2. Không cần bấm nút nào, trang tự tải lại<br>`a` 3. Địa chỉ `…/reviews?rating=rating%2Cdesc`<br>`b` 3. Địa chỉ `…/reviews?rating=rating%2Casc` | High | `a` `Rating (High > Low)`<br>`b` `Rating (Low > High)` | Yes | UI | @Regression |
| AEC_REVIEW_TC_006 | REQ-REVIEW-09 | Review / Lọc | Low | Chọn lại Filter by Rating không tải lại trang | PC-1 | 1. Mở trang `Product Reviews`<br>2. Mở ô lọc, chọn `Filter by Rating` | 2. Trang không tải lại, địa chỉ vẫn `…/reviews` | Low | — | Yes | UI | @Regression |
| AEC_REVIEW_TC_007 | REQ-REVIEW-10 | Review / Lọc | Medium | Ô lọc phải giữ lựa chọn sau khi lọc | PC-1 | 1. Mở trang `Product Reviews`<br>2. Chọn lựa chọn theo biến thể<br>3. Đọc ô lọc sau khi trang tải lại | **Mọi biến thể:** 3. Ô hiện đúng tên lựa chọn vừa chọn<br><br>Hiện tại ô quay về `Filter by Rating` (AMB-REVIEW-01) | Medium | `a` `Rating (High > Low)`<br>`b` `Rating (Low > High)` | Yes | UI | @Regression @KnownIssue @AssumptionBased |
| AEC_REVIEW_TC_008 | REQ-REVIEW-11 | Review / Lọc | High | Kết quả lọc xếp đúng theo số sao | PC-1 · PC-2 | 1. Mở trang `Product Reviews`<br>2. Chọn lựa chọn theo biến thể<br>3. Đọc cột `Rating` từ trên xuống | `a` 3. Số sao giảm dần<br>`b` 3. Số sao tăng dần<br>⚠️ chưa có evidence — bảng chưa có dữ liệu | High | `a` `Rating (High > Low)`<br>`b` `Rating (Low > High)` | Yes | UI | @Regression @NeedsVerify |
| AEC_REVIEW_TC_009 | REQ-REVIEW-06 | Review / Danh sách | High | Dòng đánh giá hiện đúng thông tin | PC-1 · PC-2 | 1. Mở trang `Product Reviews`<br>2. Tìm dòng của đánh giá test<br>3. Đối chiếu với đánh giá đã gửi ở cửa hàng | 3. `Product` là tên sản phẩm được đánh giá, `Product Owner` là chủ sản phẩm, `Customer` là tài khoản khách test, `Rating` đúng số sao, `Comment` đúng nội dung, có công tắc `Published` ⚠️ chưa có evidence | High | Đánh giá test: 4 sao, nội dung `AutoReview_TC009_1793700000` | Partial | UI | @Regression @NeedsVerify |
| AEC_REVIEW_TC_010 | REQ-REVIEW-12 | Review / Validation | Medium | Giá trị lọc sai trên địa chỉ không làm hỏng trang | PC-1 | 1. Gõ địa chỉ theo biến thể vào thanh địa chỉ, nhấn Enter<br>2. Quan sát trang | **Mọi biến thể:** 2. Vẫn ở trang quản trị `Product Reviews`, hiện danh sách mặc định; không có trang `Something went wrong!`<br><br>Hiện tại `a`, `b` ra trang lỗi của cửa hàng `Something went wrong!` · `Error code: 500` (AMB-REVIEW-03); `c` đạt (run_1793676756) | Medium | `a` `https://cms.anhtester.com/reviews?rating=xyz`<br>`b` `https://cms.anhtester.com/reviews?rating=rating,zzz`<br>`c` `https://cms.anhtester.com/reviews?rating=` | Yes | UI | @Regression @Validation @KnownIssue @AssumptionBased |
| AEC_REVIEW_TC_011 | REQ-REVIEW-13 | Review / Ẩn hiện | High | Công tắc Published lưu ngay | PC-1 · PC-2 · PC-3 | 1. Mở trang `Product Reviews`, tìm dòng đánh giá test, ghi trạng thái công tắc<br>2. Bấm công tắc `Published`<br>3. Tải lại trang<br>4. Bấm công tắc lần nữa | 2. Hiện `Published reviews updated successfully`, trang không tải lại<br>3. Công tắc giữ trạng thái mới<br>4. Hiện lại thông báo trên, công tắc về trạng thái ở bước 1 ⚠️ chưa có evidence | High | Đánh giá test `AutoReview_TC011_1793700000` | Yes | UI | @Regression @NeedsVerify @AssumptionBased |
| AEC_REVIEW_TC_012 | REQ-REVIEW-14 | Review / Ẩn hiện | High | Tắt Published thì ẩn đánh giá ở cửa hàng | PC-1 · PC-2 · PC-3 · công tắc của đánh giá test đang bật | 1. Mở trang sản phẩm ở cửa hàng, tab `reviews`, xác nhận thấy đánh giá test<br>2. Ở trang quản trị, tắt `Published` của đánh giá test<br>3. Tải lại trang sản phẩm, mở tab `reviews`<br>4. Bật lại `Published`, tải lại trang sản phẩm | 1. Thấy đánh giá test<br>3. Không còn đánh giá test<br>4. Đánh giá test hiện lại ⚠️ chưa có evidence | High | Đánh giá test `AutoReview_TC012_1793700000` | Partial | UI | @Regression @E2E @NeedsVerify @AssumptionBased |
| AEC_REVIEW_TC_013 | REQ-REVIEW-15 | Review / Nguồn dữ liệu | Low | Trang quản trị chỉ xem và ẩn hiện, không tạo đánh giá | PC-1 | 1. Mở trang `Product Reviews`, đối chiếu bảng kiểm `a`<br>2. Mở trang sản phẩm ở cửa hàng theo Test Data; nếu có popup quảng cáo, đóng bằng nút X (không bấm đăng ký, không bấm cookie)<br>3. Bấm tab `reviews` | 1. `a` Không có nút thêm, sửa, xoá đánh giá; không có `Bulk Action`; không có ô tìm<br>3. `b` Tab `reviews` hiện `There have been no reviews for this product yet.` | Low | `b` Sản phẩm `https://cms.anhtester.com/product/test-37` (hoặc sản phẩm bất kỳ chưa có đánh giá) | Yes | UI | @Regression @UI |

## V3 — Technical

| TC ID | REQ ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data | Automation | Auto Type | Tags |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| AEC_REVIEW_TC_014 | REQ-REVIEW-05 | Review / Phân quyền | High | Chưa đăng nhập không vào được trang đánh giá | Cửa sổ ẩn danh, chưa đăng nhập | 1. Gõ địa chỉ theo biến thể, nhấn Enter<br>2. Quan sát | **Mọi biến thể:** 2. Về `https://cms.anhtester.com/login`; không thấy bảng đánh giá | High | `a` `https://cms.anhtester.com/reviews`<br>`b` `https://cms.anhtester.com/reviews?rating=rating,desc` | Yes | UI | @Regression @Security |
| AEC_REVIEW_TC_015 | REQ-REVIEW-12 | Review / Bảo mật | High | Mã script, chuỗi SQL ở tham số lọc không chạy | PC-1 | 1. Gõ địa chỉ theo biến thể, nhấn Enter<br>2. Quan sát trang | **Mọi biến thể:** 2. Không bật hộp `alert`; trang không hiện thông tin kỹ thuật (tên bảng, câu truy vấn, đường dẫn tệp mã nguồn) | High | `a` `https://cms.anhtester.com/reviews?rating=<script>alert(1)</script>`<br>`b` `https://cms.anhtester.com/reviews?rating=rating,desc' OR '1'='1` | Yes | UI | @Regression @Security |
| AEC_REVIEW_TC_016 | REQ-REVIEW-05, REQ-REVIEW-08 | Review / Phiên đăng nhập | Medium | Hết phiên rồi chọn lọc thì về trang đăng nhập | PC-1 | 1. Tab 1: mở trang `Product Reviews`<br>2. Tab 2: đăng xuất<br>3. Tab 1: chọn `Rating (High > Low)` | 3. Về `https://cms.anhtester.com/login`; không thấy bảng đánh giá | Medium | — | Yes | UI | @Regression @Security |

## V4 — Non-functional

| TC ID | REQ ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data | Automation | Auto Type | Tags |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| AEC_REVIEW_TC_017 | REQ-REVIEW-04 | Review / Bố cục | Medium | Bảng co cột theo bề ngang màn hình | PC-1 | 1. Đặt cửa sổ theo biến thể<br>2. Mở trang `Product Reviews`<br>3. Đọc hàng tiêu đề bảng | `a` 3. Đủ 7 cột, không thanh cuộn ngang toàn trang<br>`b` 3. Chỉ còn cột `Product`, `Rating`; ô lọc vẫn bấm được; không thanh cuộn ngang | Medium | `a` 1920×1080<br>`b` bề ngang 800 | Partial | UI | @Regression @UI @Responsive |
| AEC_REVIEW_TC_018 | REQ-REVIEW-07, REQ-REVIEW-08 | Review / Trợ năng | Low | Lọc bằng bàn phím | PC-1 | 1. Mở trang `Product Reviews`<br>2. Nhấn Tab tới ô lọc<br>3. Nhấn Enter<br>4. Nhấn mũi tên xuống 1 lần, nhấn Enter | 2. Ô lọc có viền focus nhìn thấy được<br>3. Danh sách lựa chọn mở ra<br>4. Trang lọc theo `Rating (High > Low)` | Low | — | Yes | UI | @Regression @Accessibility |
| AEC_REVIEW_TC_019 | REQ-REVIEW-06 | Review / Đa ngôn ngữ | Low | Nội dung đánh giá tiếng Việt, emoji hiện đúng | PC-1 · PC-2 | 1. Gửi đánh giá test ở cửa hàng với nội dung theo Test Data<br>2. Mở trang `Product Reviews`, đọc cột `Comment` của dòng đó | 2. Nội dung hiện đúng nguyên văn, giữ dấu tiếng Việt và emoji ⚠️ chưa có evidence | Low | `Đánh giá thử ưu đãi 🎉 TC019 1793700000` | Partial | UI | @Regression @Localization @NeedsVerify |
