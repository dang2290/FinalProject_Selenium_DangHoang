# Test Cases — Đăng nhập · Tài khoản (tổng 41 TC · 1 nền tảng)

| Thông tin | Nội dung |
|---|---|
| Nguồn requirement | [REQUIREMENTS_LOGIN_SUMMARY.md](../../requirements/login/REQUIREMENTS_LOGIN_SUMMARY.md) · web: [requirements_login_web.md](../../requirements/login/web/requirements_login_web.md) |
| Mode · độ hạt | FULL RBT · **GỘP** — 41 TC · 59 ca kiểm (10 TC có bảng biến thể, 28 biến thể) |
| Dải TC ID đã dùng | `AEC_LOGIN_TC_001` → `AEC_LOGIN_TC_041` — mã kế tiếp `AEC_LOGIN_TC_042` |
| Phạm vi REQ | `REQ-LOGIN-01` → `REQ-LOGIN-35` (35/35) |
| Mức rủi ro · độ sâu | `Cao` → **Đầy đủ** — xem đầu file web. **Nâng thêm khi:** có tài khoản khách / seller · được phép tạo tài khoản test · có hộp thư test |
| Giả định đang áp | Q1–Q10 (≈ AMB-LOGIN-01 → 13) — user chấp nhận toàn bộ 03-11-2026. TC dựa vào giả định gắn `@AssumptionBased` |
| Môi trường | **Dùng chung** (user xác nhận 03-11-2026) — không thử sai nhiều lần với email admin, không tạo tài khoản khi chưa có PC-4 |
| Automation đã có | Selenium Java (03-11-2026): `AdminLoginTest`, `CustomerLoginTest`, `RegistrationTest`, `ForgotPasswordTest`, `SessionTest` — 32 TC, nhãn `testId` = TC ID. Chưa automate: 011, 012, 023, 027, 028, 031, 041 (thiếu dữ liệu / Chrome thật) và 010b, 032 (tránh khoá tài khoản admin). 2 lần chạy phần không cần đăng nhập: 33/40 PASS, 7 FAIL đúng lỗi đã biết |

## Bản đồ tài liệu

| Nền tảng | File | Nhóm chức năng | Số TC | TC ID | REQ bao phủ |
|---|---|---|---|---|---|
| Web | [web/test_cases_login_web.md](web/test_cases_login_web.md) | V1 Smoke · V2a Quản trị · V2b Phiên · V2c Khách · V2d Đăng ký · V2e Quên mật khẩu · V3 · V4 | 41 | 001–041 | REQ-LOGIN-01 → 35 |

## Bảng Đối Soát Coverage

| REQ | Số TC | TC | Ghi chú |
|---|---|---|---|
| REQ-LOGIN-01 | 2 | 001, 038 | ✅ |
| REQ-LOGIN-02 | 1 | 002 | ✅ |
| REQ-LOGIN-03 | 1 | 037 | ✅ `@KnownIssue` ở `a` |
| REQ-LOGIN-04 | 2 | 008a, 009a | ✅ |
| REQ-LOGIN-05 | 1 | 008b–d | ✅ |
| REQ-LOGIN-06 | 1 | 009b | ✅ |
| REQ-LOGIN-07 | 3 | 010, 035, 036 | ✅ |
| REQ-LOGIN-08 | 1 | 032 | ✅ |
| REQ-LOGIN-09 | 1 | 034 | ✅ |
| REQ-LOGIN-10 | 1 | 006 | ✅ |
| REQ-LOGIN-11 | 1 | 011 | 🟡 `@NeedsVerify` |
| REQ-LOGIN-12 | 1 | 012 | 🟡 `@NeedsVerify` — cần tài khoản test |
| REQ-LOGIN-13 | 1 | 013 | ✅ 3 biến thể |
| REQ-LOGIN-14 | 1 | 014 | ✅ `@KnownIssue` |
| REQ-LOGIN-15 | 1 | 018 | ✅ |
| REQ-LOGIN-16 | 1 | 007 | ✅ |
| REQ-LOGIN-17 | 1 | 019 | ✅ `@KnownIssue` |
| REQ-LOGIN-18 | 1 | 020 | ✅ |
| REQ-LOGIN-19 | 4 | 003, 024, 038, 040 | ✅ (`@KnownIssue` ở 040) |
| REQ-LOGIN-20 | 1 | 021 | ✅ `@KnownIssue` |
| REQ-LOGIN-21 | 1 | 022 | ✅ |
| REQ-LOGIN-22 | 1 | 015 | ✅ |
| REQ-LOGIN-23 | 1 | 023 | 🟡 `@NeedsVerify` — cần tài khoản khách |
| REQ-LOGIN-24 | 2 | 004, 024 | ✅ |
| REQ-LOGIN-25 | 1 | 025 | ✅ |
| REQ-LOGIN-26 | 1 | 026 | ✅ |
| REQ-LOGIN-27 | 1 | 027 | 🟡 `@NeedsVerify` — cần PC-4 |
| REQ-LOGIN-28 | 2 | 028, 041 | 🟡 `@NeedsVerify` — cần PC-4 |
| REQ-LOGIN-29 | 1 | 016 | ✅ |
| REQ-LOGIN-30 | 2 | 005, 024 | ✅ |
| REQ-LOGIN-31 | 1 | 029 | ✅ `@KnownIssue` |
| REQ-LOGIN-32 | 2 | 030, 039 | ✅ `@KnownIssue` |
| REQ-LOGIN-33 | 1 | 033 | ✅ `@KnownIssue` |
| REQ-LOGIN-34 | 1 | 031 | 🟡 `@NeedsVerify` — cần hộp thư test |
| REQ-LOGIN-35 | 1 | 017 | ✅ |

35/35 REQ có TC. Chiều ngược (6b) — TC gánh ≥ 2 REQ: TC_008 (REQ-04 có TC_009 chống lưng), TC_009 (REQ-04 có TC_008), TC_024 (REQ-19, 24, 30 đều có TC giao diện riêng), TC_038 (REQ-01, 19 có TC_001, 003) ✅. Mỗi biến thể của TC_008, TC_009 thuộc đúng 1 REQ (ghi ở cột trên).

## Bảng Đối Soát Evidence

| Ảnh | Trạng thái | TC dựa vào |
|---|---|---|
| [login_01_admin_bo_trong.jpg](../../requirements/login/web/evidence/login_01_admin_bo_trong.jpg) | ✅ Đủ | 001, 008a |
| [login_02_admin_sai_thong_tin.jpg](../../requirements/login/web/evidence/login_02_admin_sai_thong_tin.jpg) | ✅ Đủ | 010 |
| [login_03_khach_trang_dang_nhap.jpg](../../requirements/login/web/evidence/login_03_khach_trang_dang_nhap.jpg) | ✅ Đủ | 003, 040 |
| [login_04_khach_bo_trong.jpg](../../requirements/login/web/evidence/login_04_khach_bo_trong.jpg) | ✅ Đủ | 021 |
| [login_05_dang_ky.jpg](../../requirements/login/web/evidence/login_05_dang_ky.jpg) | ✅ Đủ | 004 |
| [login_06_quen_mat_khau.jpg](../../requirements/login/web/evidence/login_06_quen_mat_khau.jpg) | ✅ Đủ | 005 |
| [login_07_menu_tai_khoan.png](../../requirements/login/web/evidence/login_07_menu_tai_khoan.png) | 🟨 Độ phân giải thấp — đủ thấy 2 mục | 018 |
| (không có ảnh — đọc DOM / nguyên văn khi recon) | Focus mặc định · email sai định dạng · mật khẩu khoảng trắng · chuyển hướng khi đã đăng nhập · nút Back · email trùng khi đăng ký · thông báo quên mật khẩu | 002, 008b–d, 009b, 014–017, 019, 026, 029, 030, 032, 033 |

### Vùng chưa có evidence — TC gắn `@NeedsVerify`

| TC | Vùng chưa quan sát | Đề xuất |
|---|---|---|
| 011 | Remember Me | Chạy trên Chrome thật, phiên do user tự đăng nhập |
| 012 | Tạm chặn khi sai nhiều lần | Tài khoản khách test (PC-3) |
| 023 | Đăng nhập chéo khu | Tài khoản khách test |
| 027, 028, 041 | Quy tắc và kết quả đăng ký | User cho phép tạo tài khoản (PC-4) |
| 031 | Gửi link đặt lại | Hộp thư test (PC-5) |

## Đối soát loại kiểm thử (4 vòng)

| Vòng | Nhánh | Trạng thái | TC / Lý do |
|---|---|---|---|
| 1 | UI cơ bản | ✅ | 001–005 (bảng kiểm 4 màn hình), 002 (mặc định) |
| 1 | Luồng then chốt | ✅ | 006 (đăng nhập), 007 (đăng xuất) |
| 2 | Validation | ✅ | 008 (4 biến thể Email), 009 (2 biến thể Password), 021, 025, 026, 027 (3), 030 |
| 2 | Required | ✅ | 008a, 009a, 021, 025, 030 |
| 2 | Business Rule | ✅ | 010 (2), 011, 012, 022, 023 (3), 028, 031 |
| 2 | Phiên làm việc (Status Flow) | ✅ | 013 (3), 014–017, 019, 020 — đối chiếu Ma trận Trạng thái của requirements |
| 2 | Notification | ✅ | 010, 021, 022, 026, 030, 033 |
| 2 | Điều hướng | ✅ | 024 (4), 029 |
| 2 | CRUD Lifecycle | ➖ | Không có danh sách tài khoản; tạo tài khoản ở 028 |
| 2 | Modal / Dialog | ➖ | Không có hộp thoại (popup quảng cáo thuộc cửa hàng, không thuộc LOGIN) |
| 3 | Permission | ✅ | 013, 023. Seller ⏭️ — chưa có tài khoản; user quyết 03-11-2026 (Q4); rà lại khi có tài khoản (AMB-LOGIN-11) |
| 3 | Security | ✅ | 012, 019, 032, 033, 034, 035 (2) |
| 3 | Session | ✅ | 019, 020 |
| 3 | API · Database · Integration · Logging/Audit | ⏭️ | QA không có quyền — chốt 27-10-2026 ở `docs/requirements/README.md`; đội Dev xác minh; rà lại khi QA được cấp quyền |
| 4 | Accessibility | ✅ | 037 (2) |
| 4 | Responsive | ✅ | 038 (3) |
| 4 | Usability | ✅ | 039, 040 |
| 4 | Localization | ✅ | 041 |
| 4 | Reliability | ✅ | 036 |
| 4 | Performance | ⏭️ | Không có công cụ đo tải — giống các module trước; đội hạ tầng nếu cần |
| 4 | Compatibility | ⏭️ | Chỉ Chromium — user quyết 02-11-2026; rà lại khi mở rộng trình duyệt |

## Đối soát cột Automation

| Kết quả | Số TC | TC |
|---|---|---|
| Yes | 33 | Còn lại |
| Partial | 8 | 011 · 012 · 023 · 028 · 031 · 038 · 039 · 041 |
| No | 0 | — |

| TC ID | Nền tảng | Automation | Trục chặn | Điều kiện · phần kiểm tay · lý do |
|---|---|---|---|---|
| AEC_LOGIN_TC_011 | Web | Partial | 2 · đóng mở trình duyệt | Cần giữ hồ sơ trình duyệt giữa hai phiên WebDriver |
| AEC_LOGIN_TC_012 | Web | Partial | 2 · dữ liệu | Cần tài khoản khách test riêng, không chạy song song |
| AEC_LOGIN_TC_023 | Web | Partial | 2 · dữ liệu | Cần tài khoản khách test |
| AEC_LOGIN_TC_028 · 041 | Web | Partial | 2 · dữ liệu + dọn | Tạo tài khoản thật; cần cách xoá tài khoản test |
| AEC_LOGIN_TC_031 | Web | Partial | 2 · hộp thư | Cần đọc hộp thư test |
| AEC_LOGIN_TC_038 · 039 | Web | Partial | 2 · Expected cần mắt người | Tự động: không cuộn ngang, phần tử hiện. Kiểm tay: chữ đè, thông báo bị che |
| AEC_LOGIN_TC_011 · 012 · 023 · 027 · 028 · 031 · 041 | Web | ⏸️ Hoãn | — | `@NeedsVerify` — chạy tay lần đầu để chốt Expected rồi mới automate |

**Lưu ý automation:** test dùng email admin thật chỉ ở TC_006, 010b, 026, 032 — **không** chạy lặp song song các TC sai mật khẩu với email admin (có thể khoá tài khoản chung). Trang cửa hàng cần đóng popup quảng cáo trước khi thao tác. Form khách và form quản trị trùng `id="email"` / `id="password"` — Page Object tách 2 class.

**Thứ tự ưu tiên automate:** `@Smoke`/`@CriticalPath` (TC_001–007) → Validation (008–010, 021, 025, 026) → Phiên (013–020) → phần còn lại.

## Bộ chạy đề xuất

| Đợt | TC | Điều kiện |
|---|---|---|
| 1 — chưa đăng nhập | 001–005, 008–010, 013, 021, 022, 024–026, 029, 030, 032–040 | Không cần phiên. TC_010b và 032 bước 2 dùng chung **một** lần thử email admin |
| 2 — user đăng nhập **một lần** | 006 → 018 → 014–017 → 020 + 007 + 019 (gộp: tab 1 mở form brand, tab 2 đăng xuất bằng menu, Back, rồi tab 1 bấm Save) | User tự đăng nhập, không tick Remember Me |
| 3 — cần chuẩn bị | 011 (Chrome thật) · 012, 023 (tài khoản khách test) · 027, 028, 041 (quyền tạo tài khoản) · 031 (hộp thư test) | Chưa có thì chấm BLOCKED |

## Rà soát đặc tính chất lượng (ISO/IEC 25010:2023)

| Đặc tính | Trạng thái | TC ID / Lý do |
|---|---|---|
| Functional Suitability | ✅ Có TC | TC_001–031 |
| Performance Efficiency | ➖ Không áp dụng | Không có ngưỡng — đội hạ tầng nếu cần đo tải |
| Compatibility | ➖ Ngoài phạm vi | Chỉ Chromium — user quyết 02-11-2026 |
| Interaction Capability | ✅ Có TC | TC_002 (focus sẵn), 037 (bàn phím), 039, 040 |
| Reliability | ✅ Có TC | TC_020 (hết phiên), 036 (nhấp đúp) |
| Security | ✅ Có TC | TC_012, 013, 019, 023, 032–035 |
| Maintainability | ➖ Không áp dụng | Đặc tính của mã nguồn, không kiểm bằng manual TC |
| Flexibility | ✅ Có TC | TC_038 (3 kích thước), 041 (Unicode, emoji) |
| Safety | ✅ Có TC | TC_012 (chỉ dùng tài khoản test), 019 (không lộ dữ liệu sau đăng xuất) |

## Báo cáo Self-Quality Gate

| # | Tiêu chí | Kết quả |
|---|---|---|
| 0 | Độ hạt nhất quán | ✅ GỘP toàn bộ; mọi biến thể có mã; tối đa 4 biến thể/TC; TC thiết kế để FAIL (014, 019, 021, 029, 030, 033, 039, 040) tách riêng, mỗi TC một lỗi; Email và Password không gộp chung TC |
| 1 | Unique TC ID | ✅ 41 ID, không trùng, đúng mẫu `AEC_LOGIN_TC_<3 số>` |
| 2 | Step ↔ Expected 1-1 | ✅ Steps đánh số, Expected đánh số theo bước có kết quả quan sát |
| 3 | Test data cụ thể | ✅ Email `auto_login_TC<nnn>_<ts>@auto.test`; mật khẩu thật chỉ dẫn chiếu `.env`, không ghi giá trị |
| 4 | Field validation coverage | ✅ Email (bắt buộc, 3 định dạng sai, chưa đăng ký, có thật, chuỗi SQL) · Password (bắt buộc, khoảng trắng, che, chuỗi SQL) · Checkbox điều khoản (bắt buộc) · Full Name, mật khẩu đăng ký (027, `@NeedsVerify`) · Email quên mật khẩu (trống, chưa đăng ký) |
| 5 | Automation metadata | ✅ 41/41 có `Automation` · `Auto Type` · tag; 8 Partial có lý do |
| 6 | Requirement coverage | ✅ 35/35 REQ; 6b đạt |
| 7 | Evidence-verified | ✅ Đã mở 7/7 ảnh; vùng chưa quan sát gắn `@NeedsVerify` (11 TC lúc sinh; còn 7 sau review 03-11-2026 — đều chờ dữ liệu hoặc Chrome thật) |
| 8 | Ngôn ngữ kiểm chứng | ✅ Không có thuật ngữ DOM ở phần TC chính. `419 \| PAGE EXPIRED` là chữ hiển thị trên trang |
| 9 | ISO/IEC 25010:2023 | ✅ 9/9 đặc tính đã chấm |
| 10 | Đối soát 4 vòng | ✅ Mọi nhánh được chấm, không ô 🔴; `UI cơ bản` · `Validation` · `Permission` đều ✅ |

## Nhật ký thay đổi

| Ngày | Nguồn | TC ảnh hưởng | Loại | Tóm tắt |
|---|---|---|---|---|
| 03-11-2026 | `/generate-testcases-manual-rbt login` (FULL RBT) | AEC_LOGIN_TC_001 → 041 | 🟢 Thêm | Khởi tạo bộ TC web từ REQ-LOGIN-01 → 35, độ hạt GỘP, rủi ro Cao, giả định Q1–Q10 |
| 03-11-2026 | `/review-testcases login` mode FIX ([báo cáo](review/testcase_review_report_web_20261103.md)) theo [run_1793688841](../../executions/login/web/run_1793688841/execution_report.md) | 010, 011, 019, 020, 024, 032, 033, 035–038 + PC-6 | 🟡 Sửa | PC-6 thêm "chờ trang tải xong rồi đóng popup"; TC_024 tương tự; TC_010b/032 dùng chung một lần thử email admin; TC_011 chạy trên Chrome thật; ghi chú TC_019, 020, 033. TC_037 thêm `@KnownIssue` (`a`). Gỡ `@NeedsVerify` TC_035–038. Thêm mục Bộ chạy đề xuất. Không có mốc git — bản trước khi sửa ở thư mục tạm `backup_login_tc_20261103` |
| 03-11-2026 | `/generate-automation-from-testcases` (web, Selenium Java) | 32 TC | 🔵 Automation | Sinh 5 test class, nhãn `testId` = TC ID; thay 2 test ví dụ `AEC_LOGIN_EX_001/002`. Không đổi nội dung TC |
