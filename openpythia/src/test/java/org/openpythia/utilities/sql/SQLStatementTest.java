package org.openpythia.utilities.sql;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SQLStatementTest {

    @Test
    public void normalizedSQLUnchangedTest() throws IOException {

        assertNormalizedText("SELECT * FROM dual",
                             "SELECT * FROM dual");
        assertNormalizedText("SELECT * FROM dual WHERE row1 = ?",
                             "SELECT * FROM dual WHERE row1 = ?");
        assertNormalizedText("SELECT * FROM dual WHERE row1 = :1",
                             "SELECT * FROM dual WHERE row1 = :1");

    }

    @Test
    public void normalizedSQLReplacementTest() throws IOException {

        assertNormalizedText("SELECT 1 FROM dual",
                             "SELECT ? FROM dual");
        assertNormalizedText("SELECT 'test' FROM dual",
                             "SELECT ? FROM dual");

    }

    private void assertNormalizedText(String sqlText, String expectedNormalizedText) {
        SQLStatement statement = new SQLStatement("SQL ID", "Address", "Parsing Schema", 0);

        statement.setSqlText(sqlText);
        assertEquals(expectedNormalizedText, statement.getNormalizedSQLText());
    }
}
