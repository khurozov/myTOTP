package uz.khurozov.mytotp.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Util {
    public static String cssStringToData(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }
}
