package ncu.cc.iota.impl;

import ncu.cc.iota.api.IotaConstants;
import ncu.cc.iota.api.IotaStoreApi;
import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.exceptions.IotaStoreException;
import ncu.cc.iota.models.RetrieveProgressAndResult;
import ncu.cc.iota.models.StoreProgressAndResult;
import ncu.cc.iota.utils.IotaDataSplitAndComposeUtil;
import ncu.cc.iota.utils.TrytesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

public class IotaStoreApiImpl implements IotaStoreApi {
    private static final Logger logger = LoggerFactory.getLogger(IotaStoreApiImpl.class);

    private final IotaStoreBackend backend;
    private final Scheduler scheduler;

    public IotaStoreApiImpl(IotaStoreBackend backend, Scheduler scheduler) {
        this.backend = backend;
        this.scheduler = scheduler;
    }

    @Override
    public Flux<StoreProgressAndResult> store(@Null String address, @NotNull byte[] data) {
        return store(data, (stream, message) -> {
            try {
                List<String> transfers = backend.save(address, message);

                Assert.isTrue(transfers.size() == 1, "Should be exactly one");
                Assert.isTrue(transfers.get(0) != null && transfers.get(0).length() == IotaConstants.BUNDLE_TRYTES, "Length should be " + IotaConstants.BUNDLE_TRYTES);

                stream.onNext(StoreProgressAndResult.fromMessage("Next Bundle: " + transfers.get(0)));
                return transfers.get(0);
            } catch (Exception e) {
                stream.onNext(StoreProgressAndResult.fromError(e.getMessage()));
//                stream.onError(e);
                return null;
            }
        });
    }

    @Override
    public Flux<RetrieveProgressAndResult> retrieve(@NotNull String bundle) {
        if (! TrytesUtil.checkBundleFormat(bundle)) {
            return Flux.just(RetrieveProgressAndResult.fromError("Invalid bundles input"));
        } else {
            return retrieve(bundle, (stream, nextBundle) -> {
                try {
                    List<String> transactions = backend.find(nextBundle);

                    if (transactions == null || transactions.size() == 0) {
                        logger.info("transaction {} not found", nextBundle);
                        throw new IotaStoreException(IotaStoreException.ResultEnum.BUNDLE_NOT_FOUND, nextBundle);// "transaction " + nextBundle + " not found");
                    } else {
                        logger.info("{} transaction(s)", transactions.size());

                        Assert.isTrue(transactions.size() == 1, "Should be exactly one");

                        stream.onNext(RetrieveProgressAndResult.fromMessage("Next Bundle: " + nextBundle));
                        return transactions.get(0);
                    }
//            } catch (ArgumentException e) {
//                return null;
                } catch (Exception e) {
                    logger.info("Exception {}", e.getMessage());

//                    RetrieveProgressAndResult result = new RetrieveProgressAndResult(e.getMessage());
//                    result.setError(true);
//                    stream.onNext(result);
//                stream.onError(e);
                    return null;
                }
            });
        }
    }

    @Override
    public Flux<StoreProgressAndResult> store(@NotNull byte[] data, @NotNull final IotaDataSplitAndComposeUtil.PersistentService persistentService) {
        final EmitterProcessor<StoreProgressAndResult> stream = EmitterProcessor.create();

        scheduler.schedule(() -> {
            StoreProgressAndResult result = new StoreProgressAndResult();
            try {
                result.setBundle(IotaDataSplitAndComposeUtil.convertDivideAndStore(data, stream, persistentService));
                result.setDone(result.getBundle() != null);
                stream.onNext(result);
            } catch (Exception e) {
                result.setMessage(e.getMessage());
                result.setError(true);
                stream.onNext(result);
                stream.onError(e);
            }
            stream.onComplete();
        });

        return stream;
    }


    @Override
    public Flux<RetrieveProgressAndResult> retrieve(@NotNull String bundle, @NotNull final IotaDataSplitAndComposeUtil.RetrieveService retrieveService) {
        if (! TrytesUtil.checkBundleFormat(bundle)) {
            return Flux.just(RetrieveProgressAndResult.fromError("Invalid bundles input"));
        } else {
            final EmitterProcessor<RetrieveProgressAndResult> stream = EmitterProcessor.create();

            scheduler.schedule(() -> {
                try {
                    stream.onNext(RetrieveProgressAndResult.fromData(
                            IotaDataSplitAndComposeUtil.retrieveAndCombine(bundle, stream, retrieveService)
                    ));
                } catch (Exception e) {
                    stream.onNext(RetrieveProgressAndResult.fromError(e.getMessage()));
                } finally {
                    stream.onComplete();
                }
            });

            return stream;
        }
    }
}
