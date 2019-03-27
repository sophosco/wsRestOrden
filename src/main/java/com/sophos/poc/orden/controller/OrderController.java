package com.sophos.poc.orden.controller;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sophos.poc.orden.controller.client.AuditClient;
import com.sophos.poc.orden.controller.client.SecurityClient;
import com.sophos.poc.orden.model.Orders;
import com.sophos.poc.orden.model.OrdersResponse;
import com.sophos.poc.orden.model.Status;
import com.sophos.poc.orden.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OrderController {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private SecurityClient securityClient;
	
	@Autowired
	private AuditClient auditClient;
	
	public OrderController(SecurityClient securityClient, OrderRepository orderRepository, AuditClient auditClient) {
		this.securityClient = securityClient;
		this.orderRepository = orderRepository;
		this.auditClient = auditClient;
	}

	@RequestMapping(value = "/api/orden/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Status> addOrder(
			@RequestHeader(value = "X-RqUID", required = true) String xRqUID,
			@RequestHeader(value = "X-Channel", required = true) String xChannel,
			@RequestHeader(value = "X-IPAddr", required = true) String xIPAddr,
			@RequestHeader(value = "X-Sesion", required = true) String xSesion, 
			@RequestHeader(value = "X-haveToken", required = false, defaultValue = "true" ) boolean xHaveToken, 
			@RequestBody Orders orders) 
	{
		try {
			
			if((xSesion == null || xSesion.isEmpty()) || (xHaveToken && HttpStatus.UNAUTHORIZED.equals(securityClient.verifyJwtToken(xSesion).getStatusCode()))) {
				Status status = new Status("500","El token no es valido o ya expiro. Intente mas tarde", "ERROR Ocurrio una exception inesperada", null);
				return new ResponseEntity<>(status, HttpStatus.UNAUTHORIZED);
			}
			if(orders == null) {
				Status status = new Status("500", "ERROR Ocurrio una exception inesperada", "Objecto Orders es <NULL>", null);
				return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if(xRqUID == null || xChannel == null || xIPAddr == null ) {
				Status status = new Status("500", "ERROR Ocurrio una exception inesperada", "Valor <NULL> en alguna cabecera obligatorio (X-RqUID X-Channel X-IPAddr X-Sesion)", null);
				return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			orders.setId(UUID.randomUUID().toString());
			orders.setApprovalCode(System.currentTimeMillis()+"".lastIndexOf(4)+"");
			orders.setCreateDate(new Date());
			orderRepository.save(orders);
			OrdersResponse response = new OrdersResponse(orders.getApprovalCode());
			Status status = new Status("0", "Operacion Exitosa", "", response);
			
			auditClient.saveAudit(
					xSesion,
					null,
					"Realizar Orden",
					"Si el pago es exitoso, se ejecuta el registro de la Orden",
					"Modulo de Pago",
					null,
					null,
					orders
			);
			
			return new ResponseEntity<>(status, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			Status status = new Status("500", "ERROR Ocurrio una exception inesperada", e.getMessage(), null);
			return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
