package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.config.OcppServiceTestConfig;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcppServiceTestConfig.class)
@WebAppConfiguration
public class DriverTestBase {

    @Autowired
    protected DriverFactory driverFactory;
}
