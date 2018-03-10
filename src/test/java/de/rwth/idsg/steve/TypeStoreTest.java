package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.03.2018
 */
public class TypeStoreTest {

    @Test
    public void ocpp12Test() {
        ActionResponsePair actionResponse = Ocpp12TypeStore.INSTANCE.findActionResponse(new ocpp.cp._2010._08.ResetRequest());
        Assert.assertNotNull(actionResponse);
        Assert.assertEquals("Reset", actionResponse.getAction());
        Assert.assertEquals(ocpp.cp._2010._08.ResetResponse.class, actionResponse.getResponseClass());
    }

    @Test
    public void ocpp15Test() {
        ActionResponsePair actionResponse = Ocpp15TypeStore.INSTANCE.findActionResponse(new ocpp.cp._2012._06.UpdateFirmwareRequest());
        Assert.assertNotNull(actionResponse);
        Assert.assertEquals("UpdateFirmware", actionResponse.getAction());
        Assert.assertEquals(ocpp.cp._2012._06.UpdateFirmwareResponse.class, actionResponse.getResponseClass());
    }
}
