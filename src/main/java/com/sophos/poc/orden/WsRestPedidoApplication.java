package com.sophos.poc.orden;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class WsRestPedidoApplication{

	public static void main(String[] args) {
		SpringApplication.run(WsRestPedidoApplication.class, args);
	}
}
