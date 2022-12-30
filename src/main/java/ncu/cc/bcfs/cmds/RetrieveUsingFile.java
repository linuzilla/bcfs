package ncu.cc.bcfs.cmds;

import ncu.cc.commons.utils.StackTraceUtil;
import ncu.cc.iota.api.IotaStoreApi;
import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.impl.FileStoreBackendImpl;
import ncu.cc.iota.impl.IotaStoreApiImpl;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

public class RetrieveUsingFile {
    public static void main(String[] args) {
        Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(3));

        IotaStoreBackend backend = new FileStoreBackendImpl("/tmp/iota");

        IotaStoreApi iotaStoreApi = new IotaStoreApiImpl(backend, scheduler);
        //String bundle = "ORFJDCEROEPFHNMSHLADKENLNHQIPSIFRXPFDHGTNOHWEQPFHHNGBDZLNKXTINFOZBRFNIKEXPRONIUGP";
        String bundle = "ZSOFPBSZIXUELSH9VHTZHHJ9VXYZUQQZNVTYXBNEZSM9APZECOVYQNPKLFXQJVYHZCEFDSWDTJGAZHHOI";

        iotaStoreApi.retrieve(bundle)
                .map(r -> {
                    if (r.isDone()) {
                        System.out.println(new String(r.getData()));
                    } else if (! r.isError()) {
                        System.out.println(r.getMessage());
                    } else {
                        StackTraceUtil.print1(r);
                    }
                    return r;
                })
                .blockLast();

        scheduler.dispose();
    }
}
