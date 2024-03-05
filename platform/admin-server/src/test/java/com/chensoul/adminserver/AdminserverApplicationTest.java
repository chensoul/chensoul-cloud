package com.chensoul.adminserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
class AdminserverApplicationTest {
    @Test
    void contextLoads() {
    }
}