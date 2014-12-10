package org.openpythia.batch;

public class DBConnectionInformation {

    private String host;
    private String port;
    private String sid;
    private String serviceName;
    private String tnsName;
    private String user;
    private String password;
    private String filePrefix;
    private String filePath;
    private String jdbcPath;

    public DBConnectionInformation(String host,
                                   String port,
                                   String sid,
                                   String serviceName,
                                   String tnsName,
                                   String user,
                                   String password,
                                   String filePrefix,
                                   String filePath,
                                   String jdbcPath) {
        this.host = host;
        this.port = port;
        this.sid = sid;
        this.serviceName = serviceName;
        this.tnsName = tnsName;
        this.user = user;
        this.password = password;
        this.filePrefix = filePrefix;
        this.filePath = filePath;
        this.jdbcPath = jdbcPath;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
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

    public String getFilePrefix() {
        return filePrefix;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getJdbcPath() { return jdbcPath; }
}
