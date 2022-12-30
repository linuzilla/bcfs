package ncu.cc.commons.utils;

import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.1.1
 * @since 1.0
 */
public class RandomUtil {
    private static final String PASSWORD_SET = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String LETTER_OR_NUMBER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private static Random rand = new Random(System.currentTimeMillis());

    public static String generateString(int length) {
        StringBuilder stringBuilder = new StringBuilder();

        while (stringBuilder.length() < length) {
            int index = (int) (rand.nextFloat() * LETTER_OR_NUMBER.length());
            stringBuilder.append(LETTER_OR_NUMBER.charAt(index));
        }

        return stringBuilder.toString();
    }

    public static String generateString(int lo, int hi) {
        int len = hi - lo;
        return generateString((lo < hi ? lo : hi) + nextInt(len < 0 ? -len : len));
    }

    public static void setSeed(long seed) {
        rand.setSeed(seed);
    }

    public static void nextBytes(byte[] bytes) {
        rand.nextBytes(bytes);
    }

    public static int nextInt() {
        return rand.nextInt();
    }

    public static int nextInt(int bound) {
        return rand.nextInt(bound);
    }

    public static long nextLong() {
        return rand.nextLong();
    }

    public static long nextLong(long bound) {
        long value = rand.nextLong();

        if (value < 0L) {
            value = -value;
        }

        while (value < 0L) { // when value == Long.MIN_VALUE
            value = rand.nextLong();
        }

        return value % bound;
    }

    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    public static float nextFloat() {
        return rand.nextFloat();
    }

    public static double nextDouble() {
        return rand.nextDouble();
    }

    public static double nextGaussian() {
        return rand.nextGaussian();
    }

    public static IntStream ints(long streamSize) {
        return rand.ints(streamSize);
    }

    public static IntStream ints() {
        return rand.ints();
    }

    public static IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
        return rand.ints(streamSize, randomNumberOrigin, randomNumberBound);
    }

    public static IntStream ints(int randomNumberOrigin, int randomNumberBound) {
        return rand.ints(randomNumberOrigin, randomNumberBound);
    }

    public static LongStream longs(long streamSize) {
        return rand.longs(streamSize);
    }

    public static LongStream longs() {
        return rand.longs();
    }

    public static LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
        return rand.longs(streamSize, randomNumberOrigin, randomNumberBound);
    }

    public static LongStream longs(long randomNumberOrigin, long randomNumberBound) {
        return rand.longs(randomNumberOrigin, randomNumberBound);
    }

    public static DoubleStream doubles(long streamSize) {
        return rand.doubles(streamSize);
    }

    public static DoubleStream doubles() {
        return rand.doubles();
    }

    public static DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
        return rand.doubles(streamSize, randomNumberOrigin, randomNumberBound);
    }

    public static DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
        return rand.doubles(randomNumberOrigin, randomNumberBound);
    }
}
