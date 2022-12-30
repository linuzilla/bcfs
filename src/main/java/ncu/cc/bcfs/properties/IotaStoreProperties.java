package ncu.cc.bcfs.properties;

import ncu.cc.iota.api.IotaStoreConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("iota")
public class IotaStoreProperties {
    private String host;
    private String protocol;
    private String port;
    private String seed;
    private String address;
    private boolean fileStoreEmulate;
    private String basedir;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isFileStoreEmulate() {
        return fileStoreEmulate;
    }

    public void setFileStoreEmulate(boolean fileStoreEmulate) {
        this.fileStoreEmulate = fileStoreEmulate;
    }

    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public IotaStoreConfig export() {
        IotaStoreConfig config = new IotaStoreConfig();

        config.setAddress(this.address);
        config.setHost(this.host);
        config.setProtocol(this.protocol);
        config.setPort(this.port);
        config.setSeed(this.seed);

        return config;
    }
}
