package com.example.base.constant;

public final class SuccessMessage {

    private SuccessMessage() {}

    public static final class Auth {
        private Auth() {}

        public static final String REGISTER_SUCCESS = "Đăng ký tài khoản thành công.";
        public static final String VERIFY_OTP_SUCCESS = "Xác thực OTP thành công.";
        public static final String RESEND_OTP_SUCCESS = "Gửi lại OTP thành công.";
        public static final String LOGIN_SUCCESS = "Đăng nhập thành công.";
        public static final String LOGOUT_SUCCESS = "Đăng xuất thành công.";
        public static final String REFRESH_TOKEN_SUCCESS = "Cấp phát access token thành công.";
        public static final String OAUTH_LOGIN_SUCCESS = "Đăng nhập OAuth thành công.";
        public static final String FORGOT_PASSWORD_SUCCESS = "Gửi OTP quên mật khẩu thành công.";
        public static final String VERIFY_RESET_OTP_SUCCESS = "Xác thực OTP reset mật khẩu thành công.";
        public static final String RESET_PASSWORD_SUCCESS = "Đổi mật khẩu thành công.";
    }

    public static final class User {
        private User() {}

        public static final String GET_MY_INFO_SUCCESS = "Lấy thông tin người dùng thành công.";
        public static final String GET_USER_SUCCESS = "Lấy thông tin user thành công.";
        public static final String GET_USERS_SUCCESS = "Lấy danh sách user thành công.";
        public static final String CREATE_USER_SUCCESS = "Tạo user thành công.";
        public static final String UPDATE_USER_SUCCESS = "Cập nhật user thành công.";
        public static final String UPDATE_STATUS_SUCCESS = "Cập nhật trạng thái user thành công.";
        public static final String DELETE_USER_SUCCESS = "Xóa user thành công.";
    }
}
