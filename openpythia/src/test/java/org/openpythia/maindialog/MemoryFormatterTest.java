package org.openpythia.maindialog;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MemoryFormatterTest extends TestCase {

    private static final long kb = 1024;
    private static final long mb = 1024 * kb;
    private static final long gb = 1024 * mb;

    public void testFormatMemoryInBytes() throws Exception {
        Assert.assertEquals("2G12", MemoryFormatter.formatMemoryInBytes(2 * gb + 123 * mb));
        Assert.assertEquals("2G12", MemoryFormatter.formatMemoryInBytes(2 * gb + 123 * mb + 456 * kb));

        Assert.assertEquals("18M74", MemoryFormatter.formatMemoryInBytes(18 * mb + 765 * kb));
    }
}