package ru.panic.util;

import java.util.regex.Pattern;

public class PhoneNumberValidatorUtil {
    public boolean validate(String phoneNumber) {
        // Проверяем наличие символа '+' в начале номера
        if (!phoneNumber.startsWith("+")) {
            return false;
        }

        // Удаляем символ '+' для дальнейшей проверки
        phoneNumber = phoneNumber.substring(1);

        // Проверяем, является ли номер допустимым для Казахстана, Беларуси, России или Украины
        String regex = "^(7\\d{10}|38\\d{9}|375\\d{9}|380\\d{9})$";
        return Pattern.matches(regex, phoneNumber);
    }
}
