package net.shadow.utils;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SignUtils {
    public static String variable = "";


    public static void initSignText() {
        variable = "";
        for (int i = 0; i < 131; i++) {
            variable += "ࠀࠀࠀࠀࠀ";
        }
    }

    public static String getText() {
        String char2048 = (char) 2048 + "";
        return char2048.repeat(21845);
    }

    public static String getABanText() {
        IntStream chars = new Random().ints(0, 0x10FFFF + 1);
        return chars.limit(300).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
    }


}
