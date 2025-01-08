package com.developer.homestayersbackend.service.impl;

import com.africastalking.sms.Recipient;
import com.developer.homestayersbackend.config.SmsConfig;
import com.developer.homestayersbackend.config.TwilioConfig;
import com.developer.homestayersbackend.dto.OtpResponse;
import com.developer.homestayersbackend.service.api.SmsService;
import com.developer.homestayersbackend.util.PhoneNumberUtils;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SmsServiceImpl implements SmsService {

    private final TwilioConfig twilioConfig;


    @Override
    public List<Recipient> sendSms(String phoneNumber, String message) {

        try{
            SmsConfig.getSmsService().send(message,new String[]{phoneNumber},true);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }





    @Override
    public String sendSmsMessage(String phoneNumber, String message) {

        System.out.println("FormattedNumber: " + phoneNumber);
        try{
            com.twilio.type.PhoneNumber twilioPhoneNumber = new com.twilio.type.PhoneNumber(phoneNumber);
            System.out.println("TwilioPhoneNumber: " + twilioPhoneNumber);
            if(message!=null){
               Message.Status status = Message.creator(twilioPhoneNumber,twilioConfig.getMessagingServiceSID(),message).create().getStatus();
               if(status.equals(Message.Status.FAILED)){
                   System.out.println("Message Status:"+status);
                   return "Failed";
               }
               if(status.equals(Message.Status.SENT)){
                   System.out.println("Message Status:"+status);
                   return "Sent";
               }
            }

        }
        catch (Exception exception){
            return null;
        }





        return "";
    }

    @Override
    public OtpResponse sendVerificationCode(String phoneNumber, String code) {
        OtpResponse otpResponse = new OtpResponse(code,phoneNumber);
        String message = "Dear Customer, your verification code is "+ code+", valid for 5 minutes.";
        System.out.println("USer Phone: " + phoneNumber);
        String messageStatus = sendSmsMessage(phoneNumber,message);

        if(messageStatus!=null){
            return otpResponse;
        }

        else  return null;
    }
}
