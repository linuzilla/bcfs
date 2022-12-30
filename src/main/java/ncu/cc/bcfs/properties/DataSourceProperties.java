package ncu.cc.bcfs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("datasource")
public class DataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driver;
    private String dialect;
    private String ddlAuto;
    private String showSql;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDdlAuto() {
        return ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }
}
