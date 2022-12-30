package ncu.cc.iota.utils;

import ncu.cc.iota.api.IotaConstants;
import ncu.cc.iota.exceptions.IotaStoreException;
import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import org.apache.commons.codec.binary.Hex;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class IotaDataSplitAndComposeUtil {
    private static final Logger logger = LoggerFactory.getLogger(IotaDataSplitAndComposeUtil.class);

    @FunctionalInterface
    public interface PersistentService {
        String persist(final Subscriber<StoreProgressAndResult> subscriber, String message);
    }

    @FunctionalInterface
    public interface RetrieveService {
        String retrieve(final Subscriber<RetrieveProgressAndResult> subscriber, String bundle);
    }

    @FunctionalInterface
    private interface Sender {
        String send(int blockId, int from, int size, String prevBundle) throws Exception;
    }

    @FunctionalInterface
    private interface Compositor {
        String request(String bundle);
    }

    private static class IotaTrytesDivider {
        private final Sender sender;

        public IotaTrytesDivider(Sender sender) {
            this.sender = sender;
        }

        private String nonRecursiveSend(int total) throws Exception {
            int capacity = IotaConstants.ROOT_BLOCK_MAX_DATA_TRYTES;
            int from = 0;
            String bundle = null;
            int blockId = 0;

            for (int left = total; left > 0; blockId++) {
                if (left < capacity) {
                    bundle = sender.send(blockId, from, left, null);
                    break;
                } else {
                    capacity = blockId == 0 ? IotaConstants.ROOT_BLOCK_MIN_DATA_TRYTES : IotaConstants.DATA_BLOCK_MIN_DATA_TRYTES;

                    from += capacity;
                    left -= capacity;
                }
                capacity = IotaConstants.DATA_BLOCK_MAX_DATA_TRYTES;
            }

            while (--blockId >= 0) {
                if (blockId == 0) {
                    from = 0;
                    capacity = IotaConstants.ROOT_BLOCK_MIN_DATA_TRYTES;
                } else {
                    from = IotaConstants.ROOT_BLOCK_MIN_DATA_TRYTES + (blockId - 1) * IotaConstants.DATA_BLOCK_MIN_DATA_TRYTES;
                    capacity = IotaConstants.DATA_BLOCK_MIN_DATA_TRYTES;
                }

                bundle = sender.send(blockId, from, capacity, bundle);
            }
            return bundle;
        }

        private String recursiveSendx(int blockId, int from, int left) throws Exception {
            int capacity = blockId == 0 ? IotaConstants.ROOT_BLOCK_MAX_DATA_TRYTES : IotaConstants.DATA_BLOCK_MAX_DATA_TRYTES;

            if (left <= capacity) {
                return sender.send(blockId, from, left, null);
            } else {
                capacity = blockId == 0 ? IotaConstants.ROOT_BLOCK_MIN_DATA_TRYTES : IotaConstants.DATA_BLOCK_MIN_DATA_TRYTES;

                return sender.send(blockId, from, capacity, recursiveSendx(blockId + 1, from + capacity, left - capacity));
            }
        }
    }

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Assert.isTrue(false, e.getMessage());
        }
    }

    private static String decomposeMessage(final Subscriber<StoreProgressAndResult> subscriber, int blockId, String dataLength, String sha256, String nextBundle, String message) {
        //    Signature        (   1)
        //    *Data Length     (   5)
        //    *Data Sha256     (  56)
        //    Next Bundle      (  81)
        //    Data             (2043)
        //    Bundle Checksum  (   1)
        String result = IotaConstants.IOTA_STORE_SIGNATURE;

        if (blockId == 0) {
            result += dataLength;
            result += sha256;
        }

        if (nextBundle != null) {
            result += nextBundle;
        }

        result += message;

        if (result.length() < IotaConstants.TRYTES_LENGTH - 1) {
            // System.out.println("Add padding zero, len = " + result.length() + ", should be " + (IotaConstants.TRYTES_LENGTH - 1));
            for (int i = result.length(); i < IotaConstants.TRYTES_LENGTH - 1; i++) {
                result += "9";
            }
        }

        subscriber.onNext(StoreProgressAndResult.fromMessage("decompose on block " + blockId));

        int sum = (27 - TrytesUtil.caculateSum(result) % 27 + IotaConstants.BLOCK_CHECK_MAGIC_NUMBER) % 27;
        result += TrytesUtil.int2Trytes(sum, 1);

        return result;
    }

    private static boolean verifyBlock(String message) {
        if (message == null) {
            return false;
        }

        if (message.length() != IotaConstants.TRYTES_LENGTH) {
            return false;
        }
        if (! IotaConstants.IOTA_STORE_SIGNATURE.equals(message.substring(0, 1))) {
            return false;
        }

        return TrytesUtil.caculateSum(message) % 27 == IotaConstants.BLOCK_CHECK_MAGIC_NUMBER;
    }

    private static int calculateNumberOfBlocks(int dataLength) {
        int dataBits = dataLength * 8;
        int v = dataBits + IotaConstants.BI_POW_VALUE - 1;
        int x = (v - (v % IotaConstants.BI_POW_VALUE)) / IotaConstants.BI_POW_VALUE;
        int trytes = x * IotaConstants.TRI_POW_VALUE;


        if (trytes < IotaConstants.ROOT_BLOCK_MAX_DATA_TRYTES) {
            return 1;
        } else {
            int left = trytes - IotaConstants.ROOT_BLOCK_MIN_DATA_TRYTES;
            int numberOfBlocks = 1;

            while (left > 0) {
                numberOfBlocks++;

                if (left < IotaConstants.DATA_BLOCK_MAX_DATA_TRYTES) {
                    return numberOfBlocks;
                } else {
                    left -= IotaConstants.DATA_BLOCK_MIN_DATA_TRYTES;
                }
            }
            return numberOfBlocks;
        }
    }

    private static byte[] composing(final Subscriber<RetrieveProgressAndResult> subscriber, String firstBundle, Compositor compositor) throws IotaStoreException {
        String blockOne = compositor.request(firstBundle);

        if (! verifyBlock(blockOne)) {
            throw new IotaStoreException(IotaStoreException.ResultEnum.NOT_A_BLOCKCHAIN_FORMAT);
        }

        String sha256Trytes = blockOne.substring(
                IotaConstants.SIGNATURE_TRYTES + IotaConstants.DATA_LENGTH_TRYTES,
                IotaConstants.SIGNATURE_TRYTES + IotaConstants.DATA_LENGTH_TRYTES + IotaConstants.DATA_SAH256_TRYTES
        );

        int dataLength = TrytesUtil.trytes2Int(blockOne.substring(
                IotaConstants.SIGNATURE_TRYTES,
                IotaConstants.SIGNATURE_TRYTES + IotaConstants.DATA_LENGTH_TRYTES
        ));

        int numberOfBlocks = calculateNumberOfBlocks(dataLength);

        byte[] sha256sum = TrytesUtil.fromTrytes(sha256Trytes, 32);

        logger.info("<< DataLength = " + dataLength + ", number of Blocks = " + numberOfBlocks + ", sha256 = " + sha256Trytes);

        subscriber.onNext(RetrieveProgressAndResult.fromMessage("DataLength = " + dataLength + ", number of Blocks = " + numberOfBlocks + ", sha256 = " + sha256Trytes));

        StringBuffer stringBuffer = new StringBuffer();
        String nextBundle = null;

        if (numberOfBlocks > 1) {
            nextBundle = blockOne.substring(
                    IotaConstants.ROOT_BLOCK_HEADER_TRYTES,
                    IotaConstants.ROOT_BLOCK_HEADER_TRYTES + IotaConstants.BUNDLE_TRYTES
            );

            stringBuffer.append(blockOne.substring(
                    IotaConstants.ROOT_BLOCK_HEADER_TRYTES + IotaConstants.BUNDLE_TRYTES,
                    IotaConstants.ROOT_BLOCK_HEADER_TRYTES + IotaConstants.BUNDLE_TRYTES + IotaConstants.ROOT_BLOCK_MIN_DATA_TRYTES
            ));

            for (int blockId = 1; blockId < numberOfBlocks; blockId++) {
                String block = compositor.request(nextBundle);

                subscriber.onNext(RetrieveProgressAndResult.fromMessage("composing block " + blockId));

                if (! verifyBlock(block)) {
                    throw new IotaStoreException(IotaStoreException.ResultEnum.NOT_A_BLOCKCHAIN_FORMAT);
                }

                if (blockId == numberOfBlocks - 1) {
                    stringBuffer.append(block.substring(
                            IotaConstants.SIGNATURE_TRYTES,
                            IotaConstants.SIGNATURE_TRYTES + IotaConstants.DATA_BLOCK_MAX_DATA_TRYTES
                    ));
                } else {
                    nextBundle = block.substring(
                            IotaConstants.SIGNATURE_TRYTES,
                            IotaConstants.SIGNATURE_TRYTES + IotaConstants.BUNDLE_TRYTES
                    );
                    stringBuffer.append(block.substring(
                            IotaConstants.SIGNATURE_TRYTES + IotaConstants.BUNDLE_TRYTES,
                            IotaConstants.SIGNATURE_TRYTES + IotaConstants.BUNDLE_TRYTES + IotaConstants.DATA_BLOCK_MIN_DATA_TRYTES
                    ));
                }
            }
        } else {
            stringBuffer.append(blockOne.substring(
                    IotaConstants.ROOT_BLOCK_HEADER_TRYTES,
                    IotaConstants.ROOT_BLOCK_HEADER_TRYTES + IotaConstants.ROOT_BLOCK_MAX_DATA_TRYTES));
        }

        byte[] data = TrytesUtil.fromTrytes(stringBuffer.toString(), dataLength);

        System.out.println(data.length);
        final byte[] sha256digest = digest.digest(data);


        logger.info("sha256sum=" + Hex.encodeHexString(sha256digest));

        if (! Arrays.equals(sha256sum, sha256digest)) {
            throw new IotaStoreException(IotaStoreException.ResultEnum.CHECK_SUM_ERROR);
        }

        return data;
    }


    public static byte[] retrieveAndCombine(String bundle, final Subscriber<RetrieveProgressAndResult> subscriber, RetrieveService retrieveService) throws Exception {
        return composing(subscriber, bundle, nextBundle -> retrieveService.retrieve(subscriber, nextBundle));
    }

    public static String convertDivideAndStore(final byte[] data, final Subscriber<StoreProgressAndResult> subscriber, PersistentService persistentService) throws Exception {
        final String sha256 = TrytesUtil.toTrytes(digest.digest(data));
        final String trytes = TrytesUtil.toTrytes(data);

        final int len = trytes.length();
        final String dataLength = TrytesUtil.int2Trytes(data.length, IotaConstants.DATA_LENGTH_TRYTES);

        subscriber.onNext(StoreProgressAndResult.fromMessage(
                "Data Length=" + data.length + ", trytes length=" + len + ", number of blocks = " + calculateNumberOfBlocks(len)
        ));

        logger.info(">> dataLength=" + dataLength + ", len = " + len + ", sha256 = " + sha256);

        return new IotaTrytesDivider((blockId, from, size, prevBundle) -> persistentService.persist(
                subscriber,
                decomposeMessage(subscriber, blockId, dataLength, sha256, prevBundle, trytes.substring(from, from + size))
        )).nonRecursiveSend(len); //recursiveSend(0, 0, len);
    }
}
