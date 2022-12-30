package ncu.cc.bcfs.cmds;

import jota.IotaAPI;
import jota.dto.response.GetNewAddressResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Input;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.TrytesConverter;
import ncu.cc.iota.exceptions.IotaStoreException;
import ncu.cc.iota.utils.TrytesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IotaConnect {
    //private static final String IOTA_HOST = "node04.iotatoken.nl"; // "eugene.iota.community";
    private static final String IOTA_PORT = "14265"; // "443";
    private static final String IOTA_PROTOCOL = "http";
    private static final String IOTA_HOST = "140.115.0.204";
    private static final String SEED = "LBSVEBZFGJWJKJBSPCZOBWKLNI9JVANMPSUNQNNRVYHY9BROTJKGNIJJPJ9LLXRZXCYXWQBZNZOPOCXEN";
    private static final int SECURITY = 2; // 1, default 2
    private static final int DEPTH = 9; // 3, default 9
    private static final int MIN_WEIGHT_MAGNITUDE = 14; // as in the light-wallet, 15 is inWeightMagnitude which is basically the amount of PoW that is done for a transaction
    private static final String EMPTY_TAG = "999999999999999999999999999";


    // private static final String IOTA_PORT = "14700";

    public static void findTransaction(IotaAPI api, String bundle) {
        String[] bundles = new String[] { bundle };

        try {
            List<Transaction> transactions = api.findTransactionObjectsByBundle(bundles);

            for (Transaction transaction: transactions) {
                System.out.println("Address: " + transaction.getAddress());
                System.out.println("Length: " + transaction.getSignatureFragments().length());
                System.out.println("Message: " + transaction.getSignatureFragments());
                System.out.println("Value: " + transaction.getValue());
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
    }

    public static GetNewAddressResponse getNewAddress(IotaAPI api, String seed) throws ArgumentException {
        int security = 1;
        int index = 0;
        boolean checksum = true;
        int total = 1;
        boolean returnAll = false;

        return api.getNewAddress(seed, security, index, checksum, total, returnAll);
    }

    public static List<String> sendTransfer(IotaAPI api, String seed, String address) throws ArgumentException {
        long value = 0L;

        String message = TrytesConverter.toTrytes("some msg");
        Transfer transfer = new Transfer(address, value, message, EMPTY_TAG);

        List<Transfer> transfers = Arrays.asList(transfer);
        List<Input> inputs = new ArrayList<>();
        String remainderAddress = null;
        boolean validateInputs = false;

        SendTransferResponse transferResponse = api.sendTransfer(seed, SECURITY, DEPTH, MIN_WEIGHT_MAGNITUDE, transfers, inputs, remainderAddress, validateInputs);

        return transferResponse.getTransactions().stream()
                .map(Transaction::getBundle)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String msgToEncode = "<< 72&923klaMas SLfjlwqej se tkl jslkjv;alw ezsdfwosdfa >>";
        byte[] msg = msgToEncode.getBytes();

//        System.out.println(msg.length);
//        System.out.println(msg[0]);

        String s = TrytesUtil.toTrytes(msg);

        System.out.println(s);
        System.out.println("length = " + s.length());

        try {
            String s1 = new String(TrytesUtil.fromTrytes(s, msg.length));
            System.out.println(s1);
            System.out.println(msgToEncode);
        } catch (IotaStoreException e) {
            e.printStackTrace();
        }

        // System.exit(0);



        int minWeightMagnitude = 14; // as in the light-wallet
        int depth = 3;
        IotaAPI api = new IotaAPI.Builder()
                .protocol(IOTA_PROTOCOL)
                .host(IOTA_HOST)
                .port(IOTA_PORT)
                .build();

        GetNodeInfoResponse response = api.getNodeInfo();

        // String bundle = "UPKYQIGZKQ9LFEAJTHVURERH99HCFPBJXRHFWYZOBXIUH9GKEJGILUDUYUGDYIB9FVEJDPJWNGCEHOHWX";
        // String bundle = "NVNKUZBHX9F9BNJVDUITLOHYNCB9Q9BRKCORTEUDXJJWBKJTTCLKXZODYYAUWXXVLEVCWEBQZEI9LRLBC";
        String bundle = "PUWJLVHNVCKJ9CUEZPXYKZDIQSPQ9SESGPBFRXFDF9AQYXJVIKIIBAGLYVDLBWTLSZ9WQTNLPHLYYTUFX";

        System.out.println("Bundle length: " + bundle.length());
        System.out.println(response.getAppVersion());
        System.out.println(response.getNeighbors());

        findTransaction(api, bundle);

        try {
            GetNewAddressResponse newAddress = getNewAddress(api, SEED);
            System.out.println("New Address: " + newAddress.getAddresses());

            String address = "OLHIKHQLLPX9WYIRWGQKYPMUANJHXP9EVHDVXKDFPWKWYEXFOFKBGECVAWYOJRSQZYFQXFQHTTPPNRXUX";

            // sendTransfer(api, SEED, address);

//            String message =TrytesConverter.toTrytes("some msg");
//            Transfer transfer = new Transfer(address, 0, message, tag);
//
//            List<Transfer> transfers = Arrays.asList(transfer);
//            List<Input> inputs = new ArrayList<>();
//            String remainderAddress = null;
//            boolean validateInputs = false;
//
//            SendTransferResponse transferResponse = api.sendTransfer(SEED, SECURITY, depth, minWeightMagnitude, transfers, inputs, remainderAddress, validateInputs);
//
//            transferResponse.getTransactions().forEach(transaction -> {
//                System.out.println("Bundle: " + transaction.getBundle());
//            });
        } catch (ArgumentException e) {
            e.printStackTrace();
        }



        // api.sendTransfer("",1, depth, minWeightMagnitude, transfers, inputs, remainderAddress, validateInputs);

        // api.
    }
}
