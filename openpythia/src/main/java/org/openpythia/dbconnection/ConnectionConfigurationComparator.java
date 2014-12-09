package org.openpythia.dbconnection;

import org.openpythia.preferences.ConnectionConfiguration;

import java.util.Comparator;

public class ConnectionConfigurationComparator implements Comparator<ConnectionConfiguration> {

    @Override
    public int compare(ConnectionConfiguration o1, ConnectionConfiguration o2) {
        return o1.getConnectionName().compareTo(o2.getConnectionName());
    }
}
