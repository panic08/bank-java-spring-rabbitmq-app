package ru.panic.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
public class PhoneNumberValidatorUtil {
    public boolean validate(String phoneNumber) {
        if (!phoneNumber.startsWith("+")) {
            return false;
        }
        phoneNumber = phoneNumber.substring(1);
        String regex = "^(7\\d{10}|38\\d{9}|375\\d{9}|380\\d{9})$";
        return Pattern.matches(regex, phoneNumber);
    }
}
