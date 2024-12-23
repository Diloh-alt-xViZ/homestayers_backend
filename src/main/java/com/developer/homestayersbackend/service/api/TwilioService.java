package com.developer.homestayersbackend.service.api;

import com.developer.homestayersbackend.dto.OtpResponse;
import com.developer.homestayersbackend.dto.PhoneNumber;
import com.twilio.exception.TwilioException;

public interface TwilioService {
    OtpResponse sendVerificationCode(String phoneNumber, String otp)throws TwilioException;
    void sendBookingNotification(PhoneNumber phoneNumber, String message, PhoneNumber phoneNumber1) throws Exception;
    void sendBookingDenialNotification(PhoneNumber guestPhoneNumber, String message) throws Exception;
    boolean verifyCode(String phoneNumber);
    void sendBookingApprovalNotification(PhoneNumber phoneNumber, String approvalMessage) throws Exception;
}
