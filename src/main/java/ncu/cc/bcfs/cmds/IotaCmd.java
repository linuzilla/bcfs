package ncu.cc.bcfs.cmds;

import ncu.cc.iota.api.IotaStoreApi;
import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.api.IotaStoreConfig;
import ncu.cc.iota.impl.IotaStoreApiImpl;
import ncu.cc.iota.impl.IotaStoreBackendImpl;
import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import ncu.cc.iota.utils.TrytesUtil;
import org.apache.commons.io.IOUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;

public class IotaCmd {
    private static final String IOTA_PORT = "14265"; // "443";
    private static final String IOTA_PROTOCOL = "http";
    private static final String IOTA_HOST = "140.115.0.204";
    private static final String SEED = "LBSVEBZFGJWJKJBSPCZOBWKLNI9JVANMPSUNQNNRVYHY9BROTJKGNIJJPJ9LLXRZXCYXWQBZNZOPOCXEN";

    public static void main(String[] args) {
        IotaStoreConfig config = new IotaStoreConfig(
                IOTA_HOST,
                IOTA_PROTOCOL,
                IOTA_PORT,
                SEED
        );

        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(3));

        try {
            InputStream inputStream = new FileInputStream(new File("/etc/services"));
            byte[] bytes = IOUtils.toByteArray(inputStream);

            IotaStoreBackend backend = new IotaStoreBackendImpl(config);

            IotaStoreApi iotaStoreApi = new IotaStoreApiImpl(backend, scheduler);

            Deque<String> stack = new ArrayDeque<>();

            Flux<StoreProgressAndResult> resultFlux = iotaStoreApi.store(bytes, (stream, message) -> {
                stack.push(message);
                return TrytesUtil.randomBundle();
            }).map(storeProgressAndResult -> {
                System.out.println(storeProgressAndResult.getMessage());
                return storeProgressAndResult;
            }).doOnComplete(() -> {
                Flux<RetrieveProgressAndResult> progressAndResultFlux = iotaStoreApi.retrieve(TrytesUtil.randomBundle(), (stream, bundle) -> stack.pop())
                        .map(retrieveProgressAndResult -> {
                            if (retrieveProgressAndResult.isDone()) {
                                // System.out.println(new String(retrieveProgressAndResult.getData()));

                                System.out.println("retrieve had been done");
                            } else {
                                System.out.println(retrieveProgressAndResult.getMessage());
                            }
                            return retrieveProgressAndResult;
                        });

                progressAndResultFlux.blockLast();
            });

            resultFlux.blockLast();

        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduler.dispose();
    }
}
