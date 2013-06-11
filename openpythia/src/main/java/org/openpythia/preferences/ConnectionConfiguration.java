package org.openpythia.preferences;

public class ConnectionConfiguration {

    private String connectionName;
    private String host;
    private Integer port;
    private ConnectionTypeEnum connectionTypeEnum;
    private String sid;
    private String serviceName;
    private String tnsName;
    private String user;
    private String password;

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

    public ConnectionTypeEnum getConnectionTypeEnum() {
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

    // all the following methods are used by XML persistence - do not delete even they seem not to be used
    public ConnectionConfiguration() {
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public void setConnectionTypeEnum(ConnectionTypeEnum connectionTypeEnum) {
        this.connectionTypeEnum = connectionTypeEnum;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setTnsName(String tnsName) {
        this.tnsName = tnsName;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // END usage by XML persistence

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
