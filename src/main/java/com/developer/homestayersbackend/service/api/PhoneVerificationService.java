package com.developer.homestayersbackend.service.api;

import com.developer.homestayersbackend.controller.auth.AuthenticationResponse;
import com.developer.homestayersbackend.dto.PhoneNumberAuth;
import com.developer.homestayersbackend.entity.PhoneVerification;

public interface PhoneVerificationService {
    public AuthenticationResponse verifyPhone(String phone, String verificationCode);
    public PhoneVerification getPhoneVerification(String phone);

    String verifyNewPhone(PhoneNumberAuth authRequest, Long id);
}
