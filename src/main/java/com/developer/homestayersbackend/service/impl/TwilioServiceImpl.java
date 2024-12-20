package com.developer.homestayersbackend.service.impl;

import com.developer.homestayersbackend.config.TwilioConfig;
import com.developer.homestayersbackend.dto.OtpResponse;
import com.developer.homestayersbackend.dto.PhoneNumber;
import com.developer.homestayersbackend.entity.User;
import com.developer.homestayersbackend.repository.PhoneVerificationRepository;
import com.developer.homestayersbackend.repository.UserRepository;
import com.developer.homestayersbackend.service.api.TwilioService;
import com.developer.homestayersbackend.util.PhoneNumberUtils;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;


@RequiredArgsConstructor
@Service
public class TwilioServiceImpl implements TwilioService {

    private final UserRepository userRepository;
    private final PhoneVerificationRepository verificationRepository;
    private final TwilioConfig twilioConfig;
    private static final String MESSAGINGSERVICESID = "MGc586bbbe21b7b2c349fae6f12408e57c";

    @Override
    public boolean verifyCode(String phoneNumber) {
        Optional<User> user = userRepository.findByUsername(phoneNumber);
        //
        return user.isPresent();
    }

    @Override
    public OtpResponse sendVerificationCode(String phoneNumber) {
        String otp = generateOtp();
        String message = "Dear Customer, your verification code is "+ otp+", valid for 5 minutes.";
        String formattedNumber = PhoneNumberUtils.getPhoneNumber(phoneNumber).getFullNumber();
        com.twilio.type.PhoneNumber twilioPhoneNumber = new com.twilio.type.PhoneNumber(formattedNumber);
        System.out.println("Twilio Phone Number:"+twilioPhoneNumber);
        Message.creator(twilioPhoneNumber,"MG3b661a52e30568105397c695acc770d8", message).create();
        System.out.println("Verification Message Sent");
        return new OtpResponse(otp,formattedNumber);
    }
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    @Override
    public void sendBookingNotification(PhoneNumber hostPhoneNumber, String message, PhoneNumber guestPhoneNumber) {

        com.twilio.type.PhoneNumber guestPhone = new com.twilio.type.PhoneNumber(guestPhoneNumber.getFullNumber());
        com.twilio.type.PhoneNumber hostPhone = new com.twilio.type.PhoneNumber(hostPhoneNumber.getFullNumber());
        Message.creator(hostPhone,"MG3b661a52e30568105397c695acc770d8",message).create();
        Message.creator(guestPhone,"MG3b661a52e30568105397c695acc770d8",message).create();
    }

    @Override
    public void sendBookingApprovalNotification(PhoneNumber phoneNumber, String approvalMessage) {
        com.twilio.type.PhoneNumber guestPhone = new com.twilio.type.PhoneNumber(phoneNumber.getFullNumber());
        Message.creator(guestPhone,"MG3b661a52e30568105397c695acc770d8",approvalMessage).create();
    }

    @Override
    public void sendBookingDenialNotification(PhoneNumber guestPhoneNumber, String message) {
        com.twilio.type.PhoneNumber guestPhone = new com.twilio.type.PhoneNumber(guestPhoneNumber.getFullNumber());
        Message.creator(guestPhone,"MG3b661a52e30568105397c695acc770d8",message).create();
    }
}
