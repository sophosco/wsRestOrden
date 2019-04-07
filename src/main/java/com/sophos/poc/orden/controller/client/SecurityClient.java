package com.sophos.poc.orden.controller.client;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sophos.poc.orden.model.Status;
import com.sophos.poc.orden.model.security.RequestHeader;
import com.sophos.poc.orden.model.security.RequestPayload;
import com.sophos.poc.orden.model.security.TokenRequest;

@Service
public class SecurityClient{
	
	private static final Logger logger = LogManager.getLogger(SecurityClient.class);

	public ResponseEntity<Status> verifyJwtToken(String jwt) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		
		RestTemplate restTemplate = new RestTemplate(); 
		TokenRequest tokenRq = new TokenRequest();
		RequestHeader token = new RequestHeader();
		token.setToken(jwt);
		
		tokenRq.setRequestHeader(token);
		RequestPayload payload = new RequestPayload();
		payload.setId("1");
		tokenRq.setRequestPayload(payload);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-RqUID", UUID.randomUUID().toString());
		headers.set("X-Channel", "wsRestOrden");
		headers.set("X-IPAddr", "192.169.1.1");
		headers.set("Content-Type", "application/json");
		
		HttpEntity<TokenRequest> entity = new HttpEntity<TokenRequest>(tokenRq, headers);
		
		logger.info("Request Headers: "+mapper.writeValueAsString(entity.getHeaders()));
		logger.info("Request Security: "+mapper.writeValueAsString(entity.getBody()));
		ResponseEntity<String> response = 
				restTemplate.exchange(
						System.getenv("POC_SERVICE_SECURITY_VALIDATE"),
						HttpMethod.POST,
						entity,
						String.class
				);
		logger.info("Response Security["+ response.getStatusCode() +"] : "+response.getBody());
		logger.info("Response Security["+ response.getStatusCode() +"] : "+response.getHeaders());

		JSONObject json = new JSONObject(response.getBody());
		String code = (String) json.getJSONObject("responseHeader").getJSONObject("status").get("code");
		
		if(code.equals("00") ) {
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	

}
