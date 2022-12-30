package ncu.cc.commons.utils;

public class StringUtil {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }
}
