package com.helion.subscription.infrastructure;

import com.helion.subscription.infrastructure.configuration.WebServerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test-integration")
@AutoConfigureWireMock(port=0)
@SpringBootTest(classes = {WebServerConfig.class, IntegrationTestConfiguration.class})
public class AbstractRestClientTest {
}
