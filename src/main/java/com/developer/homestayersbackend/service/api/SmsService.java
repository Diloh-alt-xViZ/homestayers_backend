package com.developer.homestayersbackend.service.api;

import com.africastalking.sms.Recipient;

import java.util.List;

public interface SmsService {
        List<Recipient> sendSms(String phoneNumber, String message);
}
