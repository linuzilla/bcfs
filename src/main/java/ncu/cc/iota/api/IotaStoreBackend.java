package ncu.cc.iota.api;

import jota.error.ArgumentException;

import javax.validation.constraints.Null;
import java.util.List;

public interface IotaStoreBackend {
    List<String> find(String... bundles) throws ArgumentException;
    List<String> save(@Null String address, String message) throws ArgumentException;
}
