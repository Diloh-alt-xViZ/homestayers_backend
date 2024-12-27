package com.developer.homestayersbackend.service.impl;

import com.africastalking.sms.Recipient;
import com.developer.homestayersbackend.config.SmsConfig;
import com.developer.homestayersbackend.service.api.SmsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsServiceImpl implements SmsService {

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
}
