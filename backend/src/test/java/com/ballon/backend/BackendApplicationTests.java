package com.ballon.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ballon.backend.services.EmailService;

@SpringBootTest
class BackendApplicationTests {
	
	@MockBean
    private EmailService emailService;

	@Test
	void contextLoads() {
	}

}
