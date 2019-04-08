package com.sophos.poc.orden.controller;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sophos.poc.orden.controller.client.AuditClient;
import com.sophos.poc.orden.controller.client.SecurityClient;
import com.sophos.poc.orden.model.Encript;
import com.sophos.poc.orden.model.Orders;
import com.sophos.poc.orden.model.OrdersResponse;
import com.sophos.poc.orden.model.Status;
import com.sophos.poc.orden.repository.EncriptRepository;
import com.sophos.poc.orden.repository.OrdersRepository;

@RestController
@RequestMapping("/api/orden")
public class OrderController {

	@Autowired
	private EncriptRepository encriptRepository;
	
	@Autowired
	private OrdersRepository orderRepository;
	
	@Autowired
	private SecurityClient securityClient;
	
	@Autowired
	private AuditClient auditClient;
	
	private static final Logger logger = LogManager.getLogger(OrderController.class);
	
	public OrderController(SecurityClient securityClient, OrdersRepository orderRepository, AuditClient auditClient) {
		this.securityClient = securityClient;
		this.orderRepository = orderRepository;
		this.auditClient = auditClient;
	}
		
	@RequestMapping(value = "/add", produces = { "application/json", "application/xml" }, consumes = {"application/json", "application/xml"} , method = RequestMethod.POST)
	public ResponseEntity<Status> addOrder(
			@RequestHeader(value = "X-RqUID", required = true) String xRqUID,
			@RequestHeader(value = "X-Channel", required = true) String xChannel,
			@RequestHeader(value = "X-IPAddr", required = true) String xIPAddr,
			@RequestHeader(value = "X-Sesion", required = true) String xSesion, 
			@RequestHeader(value = "X-HaveToken", required = false, defaultValue = "true" ) boolean xHaveToken, 
			@RequestBody String order) throws IOException 
	{
		String defaultError ="ERROR Ocurrio una exception inesperada";
		boolean save = false;

		try {
			logger.info("Request: "+order);

			JSONObject jsonObject = new JSONObject(order);
			byte[] byteArray = Base64.decodeBase64(jsonObject.getString("order").getBytes());
			String decodedString = new String(byteArray);
			logger.info(decodedString);
			
			ObjectMapper mapper = new ObjectMapper();
			Orders orders = null;
			try {
				logger.info("String decode - "+decodedString);
				orders = new ObjectMapper().readValue(decodedString, Orders.class);
			} catch (Exception e1) {
				try {
					Encript encript = new Encript();
					encript.setChannel(xChannel);
					encript.setId(UUID.randomUUID().toString());
					encript.setIdSession(xSesion);
					encript.setIpAddr(xIPAddr);
					encript.setRqUID(xRqUID);
					encript.setCreateDate(new Date());
					encript.setData(jsonObject.getString("order"));
					logger.info("Request encripter: "+mapper.writeValueAsString(encript));
					encriptRepository.save(encript);
					save = true;
				}catch(Exception e) {
					logger.error("Ocurrio un error al intentar guadar la encripcion", e);
				}
			}
			
			logger.info("Headers: xSesion["+ xSesion +"] ");
			if(save) {
				logger.info("Request: "+order);
			}else {
				logger.info("Request: "+mapper.writeValueAsString(orders));
			}
			
			if((xSesion == null || xSesion.isEmpty()) || (xHaveToken && HttpStatus.UNAUTHORIZED.equals(securityClient.verifyJwtToken(xSesion).getStatusCode()))) {
				Status status = new Status("500","El token no es valido o ya expiro. Intente mas tarde", defaultError, null);
				ResponseEntity<Status> res = new ResponseEntity<>(status, HttpStatus.UNAUTHORIZED);
				logger.info("Response ["+ res.getStatusCode() +"] :"+mapper.writeValueAsString(res));
				return res;
			}
			if(!save && orders == null) {
				Status status = new Status("500", defaultError, "Objecto Orden es <NULL>", null);
				ResponseEntity<Status> res = new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
				logger.info("Response ["+ res.getStatusCode() +"] :"+mapper.writeValueAsString(res));
				return res;
			}
			
			if(xRqUID == null || xChannel == null || xIPAddr == null ||  order == null) {
				Status status = new Status("500", defaultError, "Valor <NULL> en alguna cabecera obligatorio (X-RqUID X-Channel X-IPAddr X-Sesion)", null);
				ResponseEntity<Status> res = new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
				logger.info("Response ["+ res.getStatusCode() +"] :"+mapper.writeValueAsString(res));
				return res;
			}
			
			
			if(save) {
				auditClient.saveAudit(
						xSesion,
						null,
						"Realizar Orden",
						"Si el pago es exitoso, se ejecuta el registro de la Orden",
						"Modulo de Pago",
						null,
						null,
						xHaveToken,
						orders
				);
				
				OrdersResponse response = new OrdersResponse(System.currentTimeMillis()+"".lastIndexOf(4)+"");
				Status status = new Status("0", "Operacion Exitosa", "", response);
				ResponseEntity<Status> res = new ResponseEntity<>(status, HttpStatus.OK);
				logger.info("Response ["+ res.getStatusCode() +"] :"+mapper.writeValueAsString(res));
				return res;
			}else {
				orders.setId(UUID.randomUUID().toString());
				orders.setApprovalCode(System.currentTimeMillis()+"".lastIndexOf(4)+"");
				orders.setCreateDate(new Date());
				orderRepository.save(orders);
				OrdersResponse response = new OrdersResponse(orders.getApprovalCode());
				Status status = new Status("0", "Operacion Exitosa", "", response);
				
				if(orders.getIdSession() == null || orders.getIdSession().isEmpty())
					orders.setIdSession(UUID.randomUUID().toString());
				
				orders.setCreateDate(new Date());
			
				auditClient.saveAudit(
						xSesion,
						null,
						"Realizar Orden",
						"Si el pago es exitoso, se ejecuta el registro de la Orden",
						"Modulo de Pago",
						null,
						null,
						xHaveToken,
						orders
				);
				
				ResponseEntity<Status> res = new ResponseEntity<>(status, HttpStatus.OK);
				logger.info("Response ["+ res.getStatusCode() +"] :"+mapper.writeValueAsString(res));
				return res;
			}

		} catch (Exception e) {
			logger.error(defaultError, e);
			Status status = new Status("500", defaultError, e.getMessage(), null);
			ResponseEntity<Status> res = new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
			return res;
		}
	}
}
