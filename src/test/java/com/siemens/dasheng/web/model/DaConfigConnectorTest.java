package com.siemens.dasheng.web.model;

import org.junit.Assert;
import org.junit.Test;

public class DaConfigConnectorTest {

    @Test
    public void testEquals() throws Exception {
        DaConfigConnector conn1 = new DaConfigConnector();
        conn1.setId(1L);

        DaConfigConnector conn2 = new DaConfigConnector();
        conn2.setId(1L);

        Assert.assertEquals(conn1, conn2);


    }

}