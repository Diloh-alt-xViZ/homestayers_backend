package com.developer.homestayersbackend.service.api;

import com.africastalking.sms.Recipient;
import com.developer.homestayersbackend.dto.OtpResponse;

import java.util.List;

public interface SmsService {
        List<Recipient> sendSms(String phoneNumber, String message);
        OtpResponse sendVerificationCode(String phoneNumber, String code);
        String sendSmsMessage(String phoneNumber, String message);
}
