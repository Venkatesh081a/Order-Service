package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderedItemResponse;

import reactor.core.publisher.Mono;

public interface OrderService {

	String placeOrder(OrderRequestDto orderRequestDto);

	String cancelOrder(Long orderId);

	String updateOrder(Long orderId, OrderRequestDto orderUpdateDto);

	Mono<Double> getOrderAmount(Long orderId);

	List<OrderedItemResponse> getOrderDetails(Long orderId);

	String deleteOrder(Long orderId);

}
