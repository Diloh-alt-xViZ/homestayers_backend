package com.developer.homestayersbackend.util;

import com.developer.homestayersbackend.dto.PhoneNumber;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class PhoneNumberUtils {
    private static final String defaultCountryCode = "+263";
    private static final String PHONE_NUMBER_REGEX = "^\\+?(\\d{1,3})[-.\\s]?(\\(?\\d{1,4}\\)?)[-.\\s]?\\d{1,4}([-.\\s]?\\d{1,4}){0,3}$";
    public static PhoneNumber getPhoneNumber(String phoneNumber) {
        PhoneNumber phoneNumberObj = new PhoneNumber();
        System.out.println("Input Phone Number: " + phoneNumber);

        // Add a default "+" if necessary and normalize the input
        if (!phoneNumber.startsWith("0") && !phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }
        phoneNumber = phoneNumber.replaceAll("\\s+", ""); // Remove spaces
        System.out.println("Normalized Phone Number: " + phoneNumber);

        // Updated regex for better group targeting
        final String PHONE_NUMBER_REGEX = "^\\+?(\\d{1,3})[-.\\s]?(\\d{1,4})[-.\\s]?(\\d{1,4})[-.\\s]?(\\d{1,4})?$";
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);

        if (matcher.matches()) {
            // Extract country code and number parts
            String countryCode = matcher.group(1); // Group 1: Country code
            String part1 = matcher.group(2);       // Group 2: First part of local number
            String part2 = matcher.group(3);       // Group 3: Second part of local number
            String part3 = matcher.group(4);       // Group 4: Optional third part of local number

            // Build the full local number by combining parts
            StringBuilder numberBuilder = new StringBuilder(part1);
            if (part2 != null) numberBuilder.append(part2);
            if (part3 != null) numberBuilder.append(part3);

            // Set the PhoneNumber object
            phoneNumberObj.setCountryCode(countryCode);
            phoneNumberObj.setNumber(numberBuilder.toString());

            System.out.println("Parsed Country Code: " + countryCode);
            System.out.println("Parsed Local Number: " + numberBuilder);
        } else {
            throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber);
        }

        System.out.println("Phone Number Object: " + phoneNumberObj);
        return phoneNumberObj;
    }

    public static String formatToE164(String phoneNumber,String countryCode) {
        System.out.println("Number before formatting: " + phoneNumber);
        phoneNumber = phoneNumber.replaceAll(" ", "");
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = defaultCountryCode + phoneNumber;
            return phoneNumber;
        } else {

            return phoneNumber;
        }


    }
}
