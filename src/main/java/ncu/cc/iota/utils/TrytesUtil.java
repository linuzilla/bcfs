package ncu.cc.iota.utils;

import ncu.cc.commons.utils.RandomUtil;
import ncu.cc.iota.api.IotaConstants;
import ncu.cc.iota.exceptions.IotaStoreException;

public class TrytesUtil {
    private static final String TRYTES_CHARSET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] TRYTES_CHARS = TRYTES_CHARSET.toCharArray();

    private static final int BI_POW_VALUE = 19; //  2 ^ 19 = 524,288
    private static final int TRI_POW_VALUE = 4; // 27 ^  4 = 531,441
    private static final int MAX_VALUE;

    static {
        int v = 1;
        for (int i = 0; i < BI_POW_VALUE; i++) {
            v *= 2;
        }
        MAX_VALUE = v;
    }

    public static String int2Trytes(int value, int len) {
        String result = "";

        for (int i = 0; i < len; i++) {
            int v = value % 27;
            value = (value - v) / 27;
            result = TRYTES_CHARS[v] + result;
        }
        return result;
    }

    public static int trytes2Int(String trytes) {
        int sum = 0;

        for (int i = 0; i < trytes.length(); i++) {
            char c = trytes.charAt(i);

            sum *= 27;
            sum += (c >= 'A' && c <= 'Z') ? (c - 'A' + 1) : 0;
        }

        return sum;
    }

    public static int caculateSum(String message) {
        int sum = 0;

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            sum += (c >= 'A' && c <= 'Z') ? (c - 'A' + 1) : 0;
        }
        return sum;
    }

    @FunctionalInterface
    public interface BitStreamCallback {
        void newBits(int index, boolean on);
    }

    private static class BitsHolder {
        private int value;
        private int index;

        private BitsHolder() {
            clear();
        }

        private void clear() {
            value = 0;
            index = 0;
        }

        private void addBit(int v) {
            value <<= 1;
            value |= v;
            index++;
        }
    }

    private static class BytesStore {
        private final int len;
        private final byte[] buffer;
        private int pos;

        private BytesStore(int len) {
            this.len = len;
            this.buffer = new byte[len];
            this.pos = 0;
        }

        private void store(byte data) {
            if (pos < len) {
                buffer[pos++] = data;
            }
        }
    }

    private static void asBitSteam(byte[] data, BitStreamCallback callback) {
        int index = 0;

        for (int i = 0; i < data.length; i++) {
            int value = (int) data[i];

            for (int j = 0; j < 8; j++) {
                callback.newBits(index++, (value & 0x80) != 0);
                value <<= 1;
            }
        }
    }

    private static void trytesStringAsBitStream(String trytesString, BitStreamCallback callback) throws IotaStoreException {
        int index = 0;
        int sum = 0;

        boolean[] list = new boolean[BI_POW_VALUE];

        for (int i = 0; i < trytesString.length(); i++) {
            char c = trytesString.charAt(i);
            int v = 0;

            if (c >= 'A' && c <= 'Z') {
                v = 1 + (c - 'A');
            } else if (c != '9') {
                throw new IotaStoreException(IotaStoreException.ResultEnum.ILLEGAL_CHARACTER);
            }

            sum = sum * 27 + v;

            if (i % TRI_POW_VALUE == TRI_POW_VALUE - 1) {
                if (sum >= MAX_VALUE) {
                    throw new IotaStoreException(IotaStoreException.ResultEnum.TRYTE_OUT_OF_RANGE);
                }

                for (int j = 0; j < BI_POW_VALUE; j++) {
                    list[j] = (sum & 1) == 1;
                    sum >>= 1;
                }

                for (int j = BI_POW_VALUE - 1; j >= 0; j--) {
                    callback.newBits(index++, list[j]);
                }
            }
        }

        if (sum != 0L) {
            throw new IotaStoreException(IotaStoreException.ResultEnum.ALIGNMENT_ERROR);
        }
    }

    private static void storeTrytes(StringBuilder builder, BitsHolder bitsHolder) {
        int[] value = new int[TRI_POW_VALUE];
        int trytesValue = bitsHolder.value;

        for (int i = 0; i < TRI_POW_VALUE; i++) {
            value[i] = trytesValue % 27;
            trytesValue = (trytesValue - value[i]) / 27;
        }

        for (int i = TRI_POW_VALUE - 1; i >= 0; i--) {
            builder.append(TRYTES_CHARS[value[i]]);
        }
        bitsHolder.clear();
    }

    public static String toTrytes(byte[] data) {
        StringBuilder trytesBuffer = new StringBuilder();

        final BitsHolder bitsHolder = new BitsHolder();

        asBitSteam(data, (index, on) -> {
            bitsHolder.addBit(on ? 1 : 0);
            if (index % BI_POW_VALUE == BI_POW_VALUE - 1) {
                storeTrytes(trytesBuffer, bitsHolder);
            }
        });

        if (bitsHolder.index > 0) {
            for (int i = bitsHolder.index; i < BI_POW_VALUE; i++) {
                bitsHolder.addBit(0);
            }
            storeTrytes(trytesBuffer, bitsHolder);
        }

        return trytesBuffer.toString();
    }

    public static byte[] fromTrytes(String trytesString, int length) throws IotaStoreException {
        final BitsHolder bitsHolder = new BitsHolder();
        final BytesStore bytesStore = new BytesStore(length);

        trytesStringAsBitStream(trytesString, (index, on) -> {
            bitsHolder.addBit(on ? 1 : 0);

            if (bitsHolder.index > 0 && bitsHolder.index % 8 == 0) {
                bytesStore.store((byte) bitsHolder.value);
                bitsHolder.clear();
            }
        });
        return bytesStore.buffer;
    }

    public static boolean checkBundleFormat(String bundle) {
        return bundle != null && bundle.length() == IotaConstants.BUNDLE_TRYTES && bundle.matches("[9A-Z]+");
    }

    public static String randomBundle() {
        StringBuilder stringBuilder = new StringBuilder();

        while (stringBuilder.length() < IotaConstants.BUNDLE_TRYTES) {
            int index = (int) (RandomUtil.nextFloat() * TRYTES_CHARSET.length());
            stringBuilder.append(TRYTES_CHARSET.charAt(index));
        }

        return stringBuilder.toString();
    }
}
