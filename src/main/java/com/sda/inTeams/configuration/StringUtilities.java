package com.sda.inTeams.configuration;

import java.util.Random;
import java.util.stream.IntStream;

public class StringUtilities {

    private static final Random random = new Random();
    private static final String availableChars = "abcdefghijklmnopqrstuwxyz";

    public static String getRandomWord() {
        StringBuilder newString = new StringBuilder();
        int size = random.nextInt(13) + 2;
        IntStream.range(0, size).forEach(
                ind -> newString.append(
                        availableChars.charAt(random.nextInt(availableChars.length()))));
        return newString.toString();
    }

    public static String getRandomTextString() {
        int wordCount = random.nextInt(6) + 1;
        StringBuilder newTextString = new StringBuilder();
        IntStream.range(0,wordCount).forEach(
                ind -> newTextString.append(getRandomWord()).append(" ")
        );
        return newTextString.toString();
    }

    public static String getRandomNumberAsString(int digitCount) {
        StringBuilder randomNumberAsString = new StringBuilder(String.valueOf(random.nextInt((int) Math.pow(10, digitCount - 1)) + 1));
        while (randomNumberAsString.length() < digitCount) {
            randomNumberAsString.insert(0, "0");
        }
        return randomNumberAsString.toString();
    }

}
