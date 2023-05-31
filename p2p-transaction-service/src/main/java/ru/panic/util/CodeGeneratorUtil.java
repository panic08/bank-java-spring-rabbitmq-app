package ru.panic.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGeneratorUtil {
    public int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }
}
