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
import com.sophos.poc.orden.model.Status;
import com.sophos.poc.orden.repository.OrdersRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrderControllerTest {

	@Mock
	private OrdersRepository orderRepository;
	
	@Mock
	private SecurityClient securityClient;
	
	@Mock
	private AuditClient auditClient;

	@InjectMocks
	private OrderController controller;
	
	private String orderJSON = "{\"order\": \"eyAiaWRTZXNzaW9uIjogMSwgImJpbGxpbmciOiB7ICJmaXJzdE5hbWUiOiAiZ2hqZ2hqZyIsICJsYXN0TmFtZSI6ICJnaGpnaGoiLCAibWlkZGxlTmFtZSI6ICJmZ2hmZ2giLCAiY29tcGFueSI6ICJmZ2hmaGciLCAiZW1haWwiOiAiZ2ZnaGYiLCAicGhvbmUiOiAiZmdmZ2giLCAiY291bnRyeSI6IHsgIm5hbWUiOiAiQWxhbmQgSXNsYW5kcyIsICJjb2RlIjogIkFYIiB9LCAiY2l0eSI6ICJmZ2giLCAic3RhdGUiOiAiZyIsICJ6aXAiOiAiZyIsICJhZGRyZXNzIjogImciIH0sICJkZWxpdmVyeSI6IHsgImRlbGl2ZXJ5TWV0aG9kIjogeyAidmFsdWUiOiAiZnJlZSIsICJuYW1lIjogIkZyZWUgRGVsaXZlcnkiLCAiZGVzYyI6ICIkMC4wMCAvIERlbGl2ZXJ5IGluIDcgdG8gMTQgYnVzaW5lc3MgRGF5cyIgfSB9LCAicGF5bWVudCI6IHsgImNhcmRIb2xkZXJOYW1lIjogImZnaGZnaCIsICJjYXJkTnVtYmVyIjogImdmZ2hmZ2giLCAiZXhwaXJlZE1vbnRoIjogIjAyIiwgImV4cGlyZWRZZWFyIjogIjIwMTkiLCAiY3Z2IjogImFzZnNkYWZhZHMiIH0sICJjYXJ0IjogeyAiY29tcGFyZUxpc3QiOiBudWxsLCAid2lzaExpc3QiOiBudWxsLCAicHJvZHVjdHMiOiBbIHsgImlkIjogMSwgIm5hbWUiOiAiS2V5Ym9hcmQiLCAiaW1hZ2VzIjogWwl7ImJpZyI6ImFhc2Rhc2FzZGFzZGRkIn0gXSwgIm9sZFByaWNlIjogbnVsbCwgIm5ld1ByaWNlIjogMTc1LCAiZGlzY291bnQiOiBudWxsLCAicmF0aW5nc0NvdW50IjogNCwgInJhdGluZ3NWYWx1ZSI6IDM1MCwgImRlc2NyaXB0aW9uIjogIkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0LCBjb25zZWN0ZXR1ciBhZGlwaXNjaW5nIGVsaXQuIFV0IGNvbmd1ZSBlbGVpZmVuZCBudWxsYSB2ZWwgcnV0cnVtLiBEb25lYyB0ZW1wdXMgbWV0dXMgbm9uIGVyYXQgdmVoaWN1bGEsIHZlbCBoZW5kcmVyaXQgc2VtIGludGVyZHVtLiBWZXN0aWJ1bHVtIGFudGUgaXBzdW0gcHJpbWlzIGluIGZhdWNpYnVzIG9yY2kgbHVjdHVzIGV0IHVsdHJpY2VzIHBvc3VlcmUgY3ViaWxpYSBDdXJhZS4iLCAiYXZhaWxpYmlsaXR5Q291bnQiOiA1LCAiY29sb3IiOiBbICIjNUM2QkMwIiwgIiM2NkJCNkEiLCAiIzkwQTRBRSIgXSwgInNpemUiOiBbICJTIiwgIk0iLCAiTCIsICJYTCIgXSwgIndlaWdodCI6IDE1MCwgImNhdGVnb3J5SWQiOiAyLCAiY2FydENvdW50IjogMSB9LCB7ICJpZCI6IDQsICJuYW1lIjogIlByb2R1Y3QgNCIsICJpbWFnZXMiOiBbCXsiYmlnIjoiYWFzZGFzZGQifSBdLCAib2xkUHJpY2UiOiA2NTUsICJuZXdQcmljZSI6IDM3OS45OSwgImRpc2NvdW50IjogMjAsICJyYXRpbmdzQ291bnQiOiA2LCAicmF0aW5nc1ZhbHVlIjogNTAwLCAiZGVzY3JpcHRpb24iOiAiTG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gVXQgY29uZ3VlIGVsZWlmZW5kIG51bGxhIHZlbCBydXRydW0uIERvbmVjIHRlbXB1cyBtZXR1cyBub24gZXJhdCB2ZWhpY3VsYSwgdmVsIGhlbmRyZXJpdCBzZW0gaW50ZXJkdW0uIFZlc3RpYnVsdW0gYW50ZSBpcHN1bSBwcmltaXMgaW4gZmF1Y2lidXMgb3JjaSBsdWN0dXMgZXQgdWx0cmljZXMgcG9zdWVyZSBjdWJpbGlhIEN1cmFlLiIsICJhdmFpbGliaWxpdHlDb3VudCI6IDIsICJjYXJ0Q291bnQiOiAxLCAiY2F0ZWdvcnlJZCI6IDIgfSBdLCAidG90YWxQcmljZSI6IDU1NC45OSwgInRvdGFsQ2FydENvdW50IjogMiB9IH0\"}";
	
	
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
				orderJSON
			);
		
		assertEquals(status.getBody().getCode(), "0"); 
		//TODO 
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
				orderJSON
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
				orderJSON
			);
		
		assertEquals(status.getBody().getCode(), "100");
		assertEquals(status.getBody().getMessage(), "Operacion Exitosa");
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
				orderJSON
			);
		
		assertEquals(status.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
		

}
