package com.sophos.poc.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sophos.poc.orden.controller.OrderController;
import com.sophos.poc.orden.controller.client.AuditClient;
import com.sophos.poc.orden.controller.client.SecurityClient;
import com.sophos.poc.orden.model.Orders;
import com.sophos.poc.orden.model.Status;
import com.sophos.poc.orden.repository.OrderRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrderControllerTest {

	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private SecurityClient securityClient;
	
	@Mock
	private AuditClient auditClient;

	@InjectMocks
	private OrderController controller;
	
	
	@Before
	public void setup() {
		JacksonTester.initFields(this, new ObjectMapper());
		MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void orderController_OK() throws Exception {
		when(securityClient.verifyJwtToken("Token")).thenReturn(new ResponseEntity<Status>(HttpStatus.OK));
		
		ResponseEntity<Status> status = controller.addOrder(
				UUID.randomUUID().toString(), 
				"1", 
				"192.168.1.1.",
				"Token",
				true,
				new Orders()
			);
		
		assertEquals(status.getBody().getCode(), "0");
		assertEquals(status.getBody().getMessage(), "Operacion Exitosa");
		assertEquals(status.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	public void orderController_InternalError() throws Exception {
		when(securityClient.verifyJwtToken("Token")).thenReturn(new ResponseEntity<Status>(HttpStatus.OK));
		
		ResponseEntity<Status> status = controller.addOrder(
				UUID.randomUUID().toString(), 
				"1", 
				"192.168.1.1.",
				"Token",
				true,
				null
			);
		
		assertEquals(status.getBody().getCode(), "500");
		assertEquals(status.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void orderController_Unauthorized_WithoutToken() throws Exception {
		when(securityClient.verifyJwtToken("Token")).thenReturn(new ResponseEntity<Status>(HttpStatus.OK));
		
		ResponseEntity<Status> status = controller.addOrder(
				UUID.randomUUID().toString(), 
				"1", 
				"192.168.1.1.",
				null,
				true,
				new Orders()
			);
		
		assertEquals(status.getBody().getCode(), "500");
		assertEquals(status.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void orderController_WithoutSecurity_OK() throws Exception {
		when(securityClient.verifyJwtToken("Token")).thenReturn(new ResponseEntity<Status>(HttpStatus.OK));
		
		ResponseEntity<Status> status = controller.addOrder(
				UUID.randomUUID().toString(), 
				"1", 
				"192.168.1.1.",
				"asdasd",
				false,
				new Orders()
			);
		
		assertEquals(status.getBody().getCode(), "0");
		assertEquals(status.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	public void orderController_Unauthorized_InvalidToken() throws Exception {
		when(securityClient.verifyJwtToken("Token")).thenReturn(new ResponseEntity<Status>(HttpStatus.UNAUTHORIZED));
		
		ResponseEntity<Status> status = controller.addOrder(
				UUID.randomUUID().toString(), 
				"1", 
				"192.168.1.1.",
				"Token",
				true,
				new Orders()
			);
		
		assertEquals(status.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
		

}
