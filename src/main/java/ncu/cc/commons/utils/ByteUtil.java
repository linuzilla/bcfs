package ncu.cc.commons.utils;

public class ByteUtil {
    public static final byte TRUE = (byte) 1;
    public static final byte FALSE = (byte) 0;

    public static byte byteBoolean(Boolean value) {
        return value != null && value ? TRUE : FALSE;
    }
}
