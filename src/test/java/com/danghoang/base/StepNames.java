package com.danghoang.base;

/**
 * Tên step dùng lặp ở nhiều test: một ý chỉ có một cách gọi.
 * Tiền tố Arrange / Act / Assert do BaseTest thêm, không ghi ở đây.
 */
public final class StepNames {

    // Tiền đề
    public static final String OPEN_ADMIN_LOGIN = "Mở trang đăng nhập quản trị";
    public static final String OPEN_CUSTOMER_LOGIN = "Mở trang đăng nhập khách";

    // Hành động
    public static final String CLICK_EDIT = "Bấm Edit";
    public static final String CLICK_DELETE = "Bấm Delete";
    public static final String CLICK_CANCEL = "Bấm Cancel";
    public static final String CLICK_SAVE = "Bấm Save";
    public static final String CLICK_PAGE_2 = "Bấm trang 2";
    public static final String CLICK_VIETNAMESE_TAB = "Bấm tab Vietnamese";
    public static final String OPEN_EDIT_FORM = "Mở form sửa";
    public static final String ENTER_NAME_AND_SAVE = "Nhập Name rồi bấm Save";
    public static final String RENAME_AND_SAVE = "Đổi Name rồi bấm Save";
    public static final String CLEAR_NAME_AND_SAVE = "Xoá hết Name rồi bấm Save";
    public static final String TAB2_LOGOUT = "Tab 2 đăng xuất";
    public static final String TAB1_SAVE = "Tab 1 bấm Save";

    // Kết quả mong đợi
    public static final String CREATED = "Tạo thành công";
    public static final String SAVED = "Lưu thành công";
    public static final String BACK_AT_LOGIN = "Về trang đăng nhập";
    public static final String NAME_BLOCKED = "Trình duyệt chặn ở ô Name";
    public static final String SESSION_EXPIRED = "419 PAGE EXPIRED hoặc về /login";
    public static final String DATA_UNCHANGED = "Đăng nhập lại, dữ liệu không đổi";
    public static final String SCREEN_CHECKLIST = "Đủ thành phần theo bảng kiểm";
    public static final String EDIT_CHECKLIST = "Trang sửa đủ thành phần theo bảng kiểm";
    public static final String ONE_ROW = "Đúng 1 dòng";
    public static final String DELETE_DIALOG = "Hộp xác nhận đúng nội dung";
    public static final String REJECTED_NO_PRODUCT = "Báo lỗi, không tạo sản phẩm";
    public static final String TRANSLATABLE_HINT = "Rê chuột lên biểu tượng cạnh Name hiện Translatable";
    public static final String EDIT_FORM_VERBATIM = "Form sửa đúng nguyên văn";
    public static final String LIST_SHOWS_EMOJI = "Danh sách hiện đúng emoji";
    public static final String LIST_NAME_AS_TEXT = "Danh sách: không hộp thoại, tên nguyên văn";
    public static final String EDIT_NAME_AS_TEXT = "Trang sửa: không hộp thoại, tên nguyên văn";

    private StepNames() {}
}
