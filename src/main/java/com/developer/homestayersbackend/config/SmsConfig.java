package com.developer.homestayersbackend.config;


import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SmsConfig {


    public static final String USERNAME = "homestayers";
    public static final String KEY = "atsk_dc6a4003045d8892432030375e968af4fc8d25cd28702cf3de192462c8db9ba7598d7256";


    public SmsConfig() {
        AfricasTalking.initialize(USERNAME, KEY);

    }

    public static SmsService getSmsService() {

        return AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
    }

}
