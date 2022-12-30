package ncu.cc.bcfs.cmds;

import ncu.cc.commons.utils.StackTraceUtil;
import ncu.cc.iota.api.IotaStoreApi;
import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.api.IotaStoreConfig;
import ncu.cc.iota.impl.FileStoreBackendImpl;
import ncu.cc.iota.impl.IotaStoreApiImpl;
import ncu.cc.iota.impl.IotaStoreBackendImpl;
import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import ncu.cc.iota.utils.TrytesUtil;
import org.apache.commons.io.IOUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;

public class StoreUsingFile {
    public static void main(String[] args) {
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(3));

        try {
            InputStream inputStream = new FileInputStream(new File("/etc/services"));
            byte[] bytes = IOUtils.toByteArray(inputStream);

            IotaStoreBackend backend = new FileStoreBackendImpl("/tmp/iota");

            IotaStoreApi iotaStoreApi = new IotaStoreApiImpl(backend, scheduler);

            iotaStoreApi.store(null, bytes)
                    .map(storeProgressAndResult -> {
                        StackTraceUtil.print1(storeProgressAndResult);
                        return storeProgressAndResult;
                    })
                    .blockLast();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduler.dispose();
    }
}
