package com.peakle.shuttle.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final MailSender mailSender;

    private static final String FROM_EMAIL = "peakleshuttle@gmail.com";
    private static final String SUBJECT = "[Peakle] 이메일 인증 코드";

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject(SUBJECT);
        message.setText(buildEmailBody(code));

        mailSender.send(message);
        log.info("Verification email sent to: {}", toEmail);
    }

    private String buildEmailBody(String code) {
        return """
                [Peakle 이메일 인증]

                인증 코드: %s

                위 인증 코드를 입력하여 이메일 인증을 완료해주세요.
                인증 코드는 5분간 유효합니다.

                본인이 요청하지 않은 경우 이 메일을 무시해주세요.
                """.formatted(code);
    }
}
