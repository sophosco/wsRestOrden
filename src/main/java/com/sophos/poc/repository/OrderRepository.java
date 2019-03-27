package com.sophos.poc.repository;


import org.springframework.data.repository.CrudRepository;

import com.sophos.poc.model.Orders;

public interface  OrderRepository extends CrudRepository<Orders, String>{
	
}
