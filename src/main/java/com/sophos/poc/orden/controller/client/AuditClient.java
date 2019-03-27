package com.sophos.poc.orden.controller.client;

import java.util.Date;
import java.util.UUID;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sophos.poc.orden.model.Orders;
import com.sophos.poc.orden.model.Status;
import com.sophos.poc.orden.model.audit.Audit;

@EnableAsync
@Service
public class AuditClient {

	@Async
	public ResponseEntity<Status> saveAudit(
				String IdSesion,
				String IdUsuario,
				String TipoAccion,
				String DescripcionAccion, 
				String ModuloAplicacion,
				String IdProducto,
				String IdCategoria,
				Orders order
			) throws JsonProcessingException{
		
		RestTemplate restTemplate = new RestTemplate(); 
		ObjectMapper obj = new ObjectMapper();
		
		Audit audit = new Audit(new Date(), order.getIdSession(), IdUsuario, TipoAccion, DescripcionAccion, ModuloAplicacion, IdProducto, IdCategoria, Base64.encodeBase64String(obj.writeValueAsString(order).getBytes()));
		try {
			 HttpHeaders headers = new HttpHeaders();
			 headers.set("X-RqUID", UUID.randomUUID().toString());
			 headers.set("X-Channel", "wsRestOrden");
			 headers.set("X-IPAddr", "192.169.1.1");
			 headers.set("X-Sesion", IdSesion);
			 headers.set("X-haveToken", "true");
			 headers.set("Content-Type", "application/json");
			 HttpEntity<Audit> entity = new HttpEntity<Audit>(audit, headers);
			
			 restTemplate.exchange(
					System.getenv("POC_SERVICE_AUDIT_VALIDATE"),
					HttpMethod.POST,
					entity,
					String.class);
			
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Ocurrio un error al registrar auditoria de Orden "+e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
