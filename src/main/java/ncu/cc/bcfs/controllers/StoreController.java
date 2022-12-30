package ncu.cc.bcfs.controllers;

import ncu.cc.bcfs.constants.Routes;
import ncu.cc.bcfs.services.IotaStoreService;
import ncu.cc.bcfs.services.WebFluxMultiPartFileUploader;
import ncu.cc.iota.api.IotaConstants;
import ncu.cc.iota.models.StoreProgressAndResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

@RestController
@RequestMapping(Routes.STORE)
public class StoreController {
    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private WebFluxMultiPartFileUploader fileUploader;
    @Autowired
    private IotaStoreService iotaStoreService;

    private Flux<StoreProgressAndResult> uploadFile(InetSocketAddress remoteAddress, String  address, Flux<Part> partsFlux) {
        return partsFlux
                .filter(part -> part instanceof FilePart)
                .ofType(FilePart.class)
                .flatMap(filePart -> fileUploader.saveFile(address, filePart))
                .next()
                .flatMapMany(filename -> {
                        File f = new File(filename);

                        try {
                            if (f.length() > IotaConstants.MAX_IOTA_STORE_FILE_SIZE) {
                                return Flux.just(StoreProgressAndResult.fromError("Could not store data over " +
                                        IotaConstants.MAX_IOTA_STORE_FILE_SIZE + " bytes"));
                            } else {
                                byte[] data = IOUtils.toByteArray(new FileInputStream(f), f.length());

                                if (data == null || data.length == 0) {
                                    return Flux.just(StoreProgressAndResult.fromError("empty upload"));
                                }

                                return iotaStoreService.store(remoteAddress.getHostString(), address, data);
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            return Flux.just(StoreProgressAndResult.fromError(e.getMessage()));
                        } finally {
                            f.delete();
                        }
                });
    }


    @PostMapping(value = "/{address}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<StoreProgressAndResult> storeWithAddress(ServerWebExchange serverWebExchange, @PathVariable("address") String address, @RequestBody Flux<Part> partsFlux) {
        return uploadFile(serverWebExchange.getRequest().getRemoteAddress(), address, partsFlux);
    }

    @PostMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<StoreProgressAndResult> store(ServerWebExchange serverWebExchange, @RequestBody Flux<Part> partsFlux) {
        return uploadFile(serverWebExchange.getRequest().getRemoteAddress(), null, partsFlux);
    }
}
