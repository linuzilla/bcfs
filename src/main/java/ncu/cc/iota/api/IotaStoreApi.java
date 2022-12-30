package ncu.cc.iota.api;

import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import ncu.cc.iota.utils.IotaDataSplitAndComposeUtil;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public interface IotaStoreApi {
    Flux<StoreProgressAndResult> store(@Null String address, @NotNull byte[] data);
    Flux<RetrieveProgressAndResult> retrieve(@NotNull String bundle);

    Flux<StoreProgressAndResult> store(@NotNull byte[] data, IotaDataSplitAndComposeUtil.PersistentService persistentService);
    Flux<RetrieveProgressAndResult> retrieve(@NotNull String bundle, IotaDataSplitAndComposeUtil.RetrieveService retrieveService);
}
