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
        public static final String ERR_OTP_NOT_FOUND = "OTP không chính xác.";
        public static final String ERR_OTP_EXPIRED = "Mã OTP đã hết hạn, vui lòng gửi lại mã mới.";
        public static final String ERR_OTP_COOLDOWN = "Vui lòng chờ trước khi gửi lại OTP.";
        public static final String ERR_RESET_TOKEN_INVALID = "Reset token không hợp lệ.";
        public static final String ERR_RESET_TOKEN_EXPIRED = "Reset token đã hết hạn.";
        public static final String ERR_OAUTH_NOT_CONFIGURED = "OAuth chưa được cấu hình.";
        public static final String ERR_OAUTH_INVALID_CODE = "Authorization code OAuth không hợp lệ.";
        public static final String ERR_OAUTH_EMAIL_NOT_FOUND = "Không lấy được email từ OAuth provider.";
        public static final String ERR_OAUTH_EMAIL_NOT_VERIFIED = "Email OAuth chưa được xác thực.";
    }

    public static final class User {
        private User() {}

        public static final String ERR_USER_NOT_EXISTED = "Không tìm thấy người dùng.";
        public static final String ERR_EMAIL_EXISTED = "Email đã tồn tại.";
        public static final String ERR_PHONE_EXISTED = "Số điện thoại đã tồn tại.";
        public static final String ERR_USER_DISABLED = "Tài khoản đã bị khóa.";
        public static final String ERR_USER_NOT_ACTIVE = "Tài khoản chưa được xác thực.";
    }
}
