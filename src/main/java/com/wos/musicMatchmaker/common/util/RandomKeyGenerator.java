package com.wos.musicMatchmaker.common.util;

import java.util.Random;
import java.util.UUID;

public class RandomKeyGenerator {

    public static String generateRandomKey(String prefix) {
        // 영어 대문자와 숫자로 구성된 문자열 생성
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        // UUID 생성
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // 영어 대문자와 숫자로 구성된 랜덤 키 값 반환
        return prefix + sb.toString() + uuid.substring(0, 8);
    }
}
