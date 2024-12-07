package com.asc.politicalscorecard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.annotation.IfProfileValue;

@IfProfileValue(name = "spring.profiles.active", value = "test") // Only run tests if the active profile is "test"
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml") // Explicitly load test properties
public class PoliticalScorecardApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Context loaded for testing");
    }

    @Test
    void main() {
        PoliticalScorecardApplication.main(new String[] {});
    }
}