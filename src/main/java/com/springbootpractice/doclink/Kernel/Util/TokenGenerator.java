package com.springbootpractice.doclink.Kernel.Util;

import java.security.SecureRandom;

public class TokenGenerator {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // skip confusable chars
    private static final SecureRandom random = new SecureRandom();

    public static String generateReadableToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i > 0 && i % 3 == 0) sb.append('-'); // e.g. "AB3-7DK-MN4"
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}