package ncu.cc.iota.impl;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Transaction;
import jota.model.Transfer;
import ncu.cc.bcfs.services.IotaStoreServiceImpl;
import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.api.IotaStoreConfig;
import ncu.cc.iota.utils.TrytesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IotaStoreBackendImpl implements IotaStoreBackend {
    private static final Logger logger = LoggerFactory.getLogger(IotaStoreServiceImpl.class);

    private final IotaAPI iotaAPI;
    private final IotaStoreConfig config;

    public IotaStoreBackendImpl(IotaStoreConfig config) {
        this.config = config;
        this.iotaAPI = new IotaAPI.Builder()
                .protocol(config.getProtocol())
                .host(config.getHost())
                .port(config.getPort())
                .build();

        GetNodeInfoResponse response = iotaAPI.getNodeInfo();

        logger.info("IOTA Remote Server: {}://{}:{}", config.getProtocol(), config.getHost(), config.getPort());
        logger.info("IOTA App Version: {}, Neighbors: {}", response.getAppVersion(), response.getNeighbors());
    }

    @Override
    public List<String> find(String... bundles) throws ArgumentException {
        return iotaAPI.findTransactionObjectsByBundle(bundles).stream()
                .map(Transaction::getSignatureFragments)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> save(@Null String address, String message) throws ArgumentException {
        Transfer transfer = new Transfer(
                TrytesUtil.checkBundleFormat(address) ? address : config.getAddress(),
                0L,
                message,
                config.getTag()
        );

        SendTransferResponse transferResponse = iotaAPI.sendTransfer(
                config.getSeed(),
                config.getSecurity(),
                config.getDepth(),
                config.getMinWeightMagnitude(),
                Arrays.asList(transfer),
                Collections.EMPTY_LIST,
                null,
                false
        );

        return transferResponse.getTransactions().stream()
                .map(Transaction::getBundle)
                .collect(Collectors.toList());
    }
}
