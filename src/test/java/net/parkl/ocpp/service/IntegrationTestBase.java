package net.parkl.ocpp.service;

import net.parkl.ocpp.service.config.OcppServiceTestConfig;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OcppServiceTestConfig.class)
@WebAppConfiguration
public class IntegrationTestBase {
}
