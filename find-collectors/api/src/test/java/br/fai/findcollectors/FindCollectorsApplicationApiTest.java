package br.fai.findcollectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "JWT_SECRET=test-secret-with-at-least-thirty-two-bytes")
class FindCollectorsApplicationApiTest {

    @Test
    void contextLoads() {
    }
}
