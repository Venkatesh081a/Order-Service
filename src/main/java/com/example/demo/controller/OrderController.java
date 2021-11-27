package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderedItemResponse;
import com.example.demo.service.OrderService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;
	

	@RequestMapping(value = "/order", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
		String order = orderService.placeOrder(orderRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@RequestMapping(value = "/cancel/{orderId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
		String status = orderService.cancelOrder(orderId);
		return ResponseEntity.status(HttpStatus.OK).body(status);
	}

	@RequestMapping(value = "/update/{orderId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateOrder(@PathVariable Long orderId, @RequestBody OrderRequestDto orderUpdateDto) {
		String status = orderService.updateOrder(orderId, orderUpdateDto);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(status);
	}

	@RequestMapping(value = "/getAmount/{orderId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Double> getTotalOrderAmount(@PathVariable Long orderId) {
		Mono<Double> amount = orderService.getOrderAmount(orderId);
		Double[] result = { 0.0 };
		amount.subscribe(i -> {
			result[0] += i;
		});
		return ResponseEntity.status(HttpStatus.OK).body(result[0]);

	}

	@RequestMapping(value = "/getDetails/{orderId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrderedItemResponse>> getOrderDetails(@PathVariable Long orderId) {
		List<OrderedItemResponse> response = orderService.getOrderDetails(orderId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@RequestMapping(value = "/delete/{orderId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
		String status = orderService.deleteOrder(orderId);
		return ResponseEntity.status(HttpStatus.OK).body(status);
	}
}
