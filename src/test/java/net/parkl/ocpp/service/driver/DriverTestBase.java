package net.parkl.ocpp.service.driver;

import net.parkl.ocpp.service.config.OcppServiceTestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = OcppServiceTestConfig.class)
public class DriverTestBase {

    @Autowired
    protected DriverFactory driverFactory;
}
