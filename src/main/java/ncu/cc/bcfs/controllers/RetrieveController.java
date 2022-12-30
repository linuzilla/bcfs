package ncu.cc.bcfs.controllers;

import ncu.cc.bcfs.constants.Routes;
import ncu.cc.bcfs.services.IotaStoreService;
import ncu.cc.iota.models.RetrieveProgressAndResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(Routes.RETRIEVE)
public class RetrieveController {
    @Autowired
    private IotaStoreService iotaStoreService;

    @GetMapping(value = "{bundle}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<RetrieveProgressAndResult> retrieve(@PathVariable String bundle) {
        return iotaStoreService.retrieve(bundle);
    }
}
