package ncu.cc.iota.api;

public class IotaStoreConfig {
    private String host;
    private String protocol = "http";
    private String port;
    private String seed;
    private String address;
    private int security = IotaConstants.SECURITY;
    private int depth = IotaConstants.DEPTH;
    private int minWeightMagnitude = IotaConstants.MIN_WEIGHT_MAGNITUDE;
    private String tag = IotaConstants.EMPTY_TAG;

    public IotaStoreConfig() {
    }

    public IotaStoreConfig(String host, String protocol, String port) {
        this.host = host;
        this.protocol = protocol;
        this.port = port;
    }

    public IotaStoreConfig(String host, String protocol, String port, String seed) {
        this.host = host;
        this.protocol = protocol;
        this.port = port;
        this.seed = seed;
    }

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

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMinWeightMagnitude() {
        return minWeightMagnitude;
    }

    public void setMinWeightMagnitude(int minWeightMagnitude) {
        this.minWeightMagnitude = minWeightMagnitude;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
