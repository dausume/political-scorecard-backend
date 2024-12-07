package com.asc.politicalscorecard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.asc.politicalscorecard.PoliticalScorecardApplication.baseController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(baseController.class)
public class BaseControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        System.out.println("Setting up the test.");
    }

    
    @Test
    void testPoliticalScorecardTestEndpoint() throws Exception {
        System.out.println("Testing the base endpoint.");
        mockMvc
            .perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Successfully hit the Political Scorecard Backend!"));
    }
            
}