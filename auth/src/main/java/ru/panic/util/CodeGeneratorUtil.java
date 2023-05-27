package ru.panic.util;

import java.util.Random;

public class CodeGeneratorUtil {
    public int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }
}
