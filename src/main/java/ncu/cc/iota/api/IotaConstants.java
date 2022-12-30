package ncu.cc.iota.api;

public class IotaConstants {
    // sha256 -> 14 * 4 = 56 trytes
    // length -> 5 trytes (27^5 = 14,348,907) 71744
    // next   -> 81 trytes
    // ---------------
    // root block
    //    Signature         (   1)
    //    *Data Length      (   5)
    //    *Data Sha256      (  56)
    //    Next Bundle       (  81)
    //    Data              (2043)
    //    Bundle Checksum   (   1)
    // data block
    //    Signature         (   1)
    //    Next Bundle       (  81)
    //    Data              (2104)
    //    Bundle Checksum   (   1)
    public static final int BI_POW_VALUE = 19; //  2 ^ 19 = 524,288
    public static final int TRI_POW_VALUE = 4; // 27 ^  4 = 531,441

    public static final int BLOCK_CHECK_MAGIC_NUMBER = 1; // Between 0 ~ 26

    public static final String IOTA_STORE_SIGNATURE = "A"; // 9, A-Z
    public static final int TRYTES_LENGTH = 2187;
    public static final int BUNDLE_TRYTES = 81;

    public static final int SIGNATURE_TRYTES = 1;
    public static final int BUNDLE_CHECKSUM_TRYTES = 1;
    public static final int DATA_LENGTH_TRYTES = 5;
    public static final int DATA_SAH256_TRYTES = 56;
    // public static final int NUMBER_OF_BLOCKS_TRYTES = 4;
    public static final int DATA_BLOCK_MAX_DATA_TRYTES = TRYTES_LENGTH - SIGNATURE_TRYTES - BUNDLE_CHECKSUM_TRYTES;
    public static final int DATA_BLOCK_MIN_DATA_TRYTES = DATA_BLOCK_MAX_DATA_TRYTES - BUNDLE_TRYTES;
    public static final int ROOT_BLOCK_MAX_DATA_TRYTES = DATA_BLOCK_MAX_DATA_TRYTES - DATA_LENGTH_TRYTES - DATA_SAH256_TRYTES;
    public static final int ROOT_BLOCK_MIN_DATA_TRYTES = ROOT_BLOCK_MAX_DATA_TRYTES - BUNDLE_TRYTES;
    public static final int ROOT_BLOCK_HEADER_TRYTES = SIGNATURE_TRYTES + DATA_LENGTH_TRYTES + DATA_SAH256_TRYTES;

    public static final int MAX_IOTA_STORE_FILE_SIZE = 14_348_906; // 27 ^ 5 - 1

    public static final int SECURITY = 2; // 1, default 2
    public static final int DEPTH = 9; // 3, default 9
    public static final int MIN_WEIGHT_MAGNITUDE = 14; // as in the light-wallet, 15 is inWeightMagnitude which is basically the amount of PoW that is done for a transaction
    public static final String EMPTY_TAG = "999999999999999999999999999";
}
