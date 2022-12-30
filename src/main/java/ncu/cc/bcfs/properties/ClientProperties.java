package ncu.cc.bcfs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("application")
public class ClientProperties {
    public static class ClientEntry {
        private String user;
        private String password;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
    private Map<String,ClientEntry> clients;

    public Map<String, ClientEntry> getClients() {
        return clients;
    }

    public void setClients(Map<String, ClientEntry> clients) {
        this.clients = clients;
    }
}
