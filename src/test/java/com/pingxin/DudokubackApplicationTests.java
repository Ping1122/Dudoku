package com.pingxin;

import static org.assertj.core.api.Assertions.assertThat;

import com.pingxin.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DudokubackApplicationTests {

	@Autowired
	private AuthController controller;

	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

}
