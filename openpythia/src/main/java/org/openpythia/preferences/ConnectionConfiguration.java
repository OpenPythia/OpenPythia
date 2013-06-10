package org.openpythia.preferences;

public class ConnectionConfiguration {

    private String connectionName;
    private String host;
    private Integer port;
    private String databaseNameX;
    private ConnectionTypeEnum connectionTypeEnum;
    private String sid;
    private String serviceName;
    private String tnsName;
    private String user;
    private String password;

    // used by persistence - do not delete even it seem not to be used
    public ConnectionConfiguration() {

    }

    public ConnectionConfiguration(String connectionName, String host, Integer port,
                                   ConnectionTypeEnum connectionTypeEnum, String sid, String serviceName, String tnsName,
                                   String user, String password) {
        this.connectionName = connectionName;
        this.host = host;
        this.port = port;
        this.connectionTypeEnum = connectionTypeEnum;
        this.sid = sid;
        this.serviceName = serviceName;
        this.tnsName = tnsName;
        this.user = user;
        this.password = password;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public ConnectionTypeEnum getConnectionType() {
        return connectionTypeEnum;
    }

    public String getSid() {
        return sid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTnsName() {
        return tnsName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String toConnectionString() {
        switch (connectionTypeEnum) {
            case SID:
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, sid);
            case ServiceName:
                return String.format("jdbc:oracle:thin:@%s:%d/%s", host, port, serviceName);
            case TNSName:
                return String.format("jdbc:oracle:thin:@%s:%d/%s", host, port, tnsName);
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionConfiguration that = (ConnectionConfiguration) o;

        if (connectionName != null ? !connectionName.equals(that.connectionName) : that.connectionName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return connectionName != null ? connectionName.hashCode() : 0;
    }
}
