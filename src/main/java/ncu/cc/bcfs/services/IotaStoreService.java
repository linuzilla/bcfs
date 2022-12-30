package ncu.cc.bcfs.services;

import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface IotaStoreService {
    Flux<RetrieveProgressAndResult> retrieve(String bundle);
    Flux<StoreProgressAndResult> store(String remoteAddress, String address, byte[] data);
//    Flux<StoreProgressAndResult> store(String address, Mono<String> filename);
}
