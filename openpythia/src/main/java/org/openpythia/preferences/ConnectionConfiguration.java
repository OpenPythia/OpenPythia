package org.openpythia.preferences;

public class ConnectionConfiguration {

    private String  connectionName;
    private String  host;
    private Integer port;
    private String  databaseName;
    private String  user;
    private String  password;

    public ConnectionConfiguration() {
    }

    public ConnectionConfiguration(String connectionName, String host, Integer port, String databaseName, String user, String password) {
        this.connectionName = connectionName;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

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

    public String toConnectionString() {
        return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databaseName);
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
