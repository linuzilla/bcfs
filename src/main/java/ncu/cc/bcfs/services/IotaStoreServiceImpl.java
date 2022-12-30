package ncu.cc.bcfs.services;

import ncu.cc.bcfs.entities.Silo;
import ncu.cc.bcfs.properties.IotaStoreProperties;
import ncu.cc.bcfs.repositories.SiloRepository;
import ncu.cc.bcfs.security.SecurityService;
import ncu.cc.iota.api.IotaStoreApi;
import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.impl.FileStoreBackendImpl;
import ncu.cc.iota.impl.IotaStoreApiImpl;
import ncu.cc.iota.impl.IotaStoreBackendImpl;
import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

@Service
@EnableConfigurationProperties(IotaStoreProperties.class)
public class IotaStoreServiceImpl implements IotaStoreService {
    private static final Logger logger = LoggerFactory.getLogger(IotaStoreServiceImpl.class);

    private final IotaStoreApi iotaStoreApi;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private SiloRepository siloRepository;

    public IotaStoreServiceImpl(IotaStoreProperties properties, Scheduler scheduler) {
        IotaStoreBackend backend =
                properties.isFileStoreEmulate()
                        ? new FileStoreBackendImpl(properties.getBasedir())
                        : new IotaStoreBackendImpl(properties.export());

        logger.info("IotaStore Backend: " + backend.getClass().getSimpleName());

        iotaStoreApi = new IotaStoreApiImpl(backend, scheduler);
    }

    @Override
    public Flux<RetrieveProgressAndResult> retrieve(String bundle) {
        return iotaStoreApi.retrieve(bundle);
    }

    @Override
    public Flux<StoreProgressAndResult> store(String remoteAddress, String address, byte[] data) {
        return securityService.currentUser()
                .flatMapMany(user -> iotaStoreApi.store(address, data)
                            .map(result -> {
                                logger.info("upload file by {} from {}, size = {}, bundle = {}", user, remoteAddress, data.length, result.getBundle());

                                if (result.isDone()) {
                                    Silo silo = new Silo();
                                    silo.setBundle(result.getBundle());
                                    silo.setSize(data.length);
                                    silo.setRemoteAddress(remoteAddress);
                                    silo.setUser(user);

                                    siloRepository.save(silo);
                                }
                                return result;
                            })
                );

    }
}
