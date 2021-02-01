package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.config.OcppServiceTestConfig;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = OcppServiceTestConfig.class)
@Ignore
public class DriverTestBase {

    @Autowired
    protected DriverFactory driverFactory;
}
