package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverTestBase extends IntegrationTestBase {

    @Autowired
    protected DriverFactory driverFactory;
}
