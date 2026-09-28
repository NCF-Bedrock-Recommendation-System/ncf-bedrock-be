package com.example.base.service.impl;

import com.example.base.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Override
    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(to);
        message.setSubject("Mã OTP xác thực");
        message.setText("""
            Xin chào,

            Mã OTP của bạn là: %s

            Mã này có hiệu lực trong 5 phút.
            Vui lòng không chia sẻ mã này cho bất kỳ ai.

            Trân trọng.
            """.formatted(otp));

        mailSender.send(message);
    }
}