package com.example.base.constant;

public final class ErrorMessage {

    private ErrorMessage() {}

    public static final String ERR_EXCEPTION_GENERAL = "Đã xảy ra lỗi hệ thống.";
    public static final String UNAUTHORIZED = "Bạn cần đăng nhập để thực hiện thao tác này.";
    public static final String FORBIDDEN = "Bạn không có quyền thực hiện thao tác này.";
    public static final String BAD_REQUEST = "Dữ liệu yêu cầu không hợp lệ.";

    public static final class Auth {
        private Auth() {}

        public static final String ERR_INVALID_CREDENTIALS = "Email hoặc mật khẩu không chính xác.";
        public static final String ERR_TOKEN_INVALIDATED = "Token đã hết hiệu lực.";
        public static final String ERR_TOKEN_ALREADY_INVALIDATED = "Token đã được đăng xuất trước đó.";
        public static final String ERR_MALFORMED_TOKEN = "Token không đúng định dạng.";
        public static final String INVALID_REFRESH_TOKEN = "Refresh token không hợp lệ.";
    }

    public static final class User {
        private User() {}

        public static final String ERR_USER_NOT_EXISTED = "Không tìm thấy người dùng.";
        public static final String ERR_EMAIL_EXISTED = "Email đã tồn tại.";
        public static final String ERR_PHONE_EXISTED = "Số điện thoại đã tồn tại.";
        public static final String ERR_USER_DISABLED = "Tài khoản đã bị khóa.";
    }
}
