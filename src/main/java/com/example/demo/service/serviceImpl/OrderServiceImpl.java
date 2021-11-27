package com.example.demo.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.MenuItemResponseDto;
import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderedItemRequestDto;
import com.example.demo.dto.OrderedItemResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderedItem;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderedItemRepository;
import com.example.demo.service.OrderService;

import reactor.core.publisher.Mono;

@Service
@RefreshScope
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderedItemRepository orderItemRepository;

	@Autowired
	@Lazy
	private WebClient.Builder webClientBuilder;

	ExecutorService executorService = Executors.newFixedThreadPool(10);

	public static final String ORDER_STATUS_CREATED = "CREATED";

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Value("${microservice.restaurant-service.endpoints.endpoint.uri}")
	private String ENDPOINT_URL;

	@Override
	public String placeOrder(OrderRequestDto orderRequestDto) {
		Order order = new Order();
		logger.info("calling restaurant microservice to get the restaurant details");
		CompletableFuture<List<MenuItemResponseDto>> menuItemsList = CompletableFuture.supplyAsync(() -> {
			try {
				return webClientBuilder
						.build()
						.get()
						.uri(ENDPOINT_URL + orderRequestDto.getRestaurantId())
						.retrieve()
						.bodyToMono(new ParameterizedTypeReference<List<MenuItemResponseDto>>() {})
						.block();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}, executorService);
		menuItemsList.completeOnTimeout(null, 1, TimeUnit.SECONDS);
		logger.info("Completed calling restaurant microservice to get the restaurant details");
		order.setRestaurantId(orderRequestDto.getRestaurantId());
		order.setCustomerId(orderRequestDto.getCustomerId());
		order.setOrderStatus(ORDER_STATUS_CREATED);
		List<OrderedItemRequestDto> orderItems = orderRequestDto.getItems();
		menuItemsList.thenAccept(menuItems -> {
			orderItems.forEach(item -> {
				menuItems.forEach(menuItem -> {
					if (item.getItemId() == menuItem.getMenuItemId()) {
						orderRepository.save(order);
						OrderedItem orderedItem = new OrderedItem();
						orderedItem.setItemId(menuItem.getMenuItemId());
						orderedItem.setItemName(menuItem.getMenuItemName());
						orderedItem.setQuantity(item.getQuantity());
						orderedItem.setTotalPrice(item.getQuantity() * menuItem.getPrice());
						orderedItem.setOrder(order);
						orderItemRepository.save(orderedItem);
					}
				});
			});
		}).thenRun(() -> {
			logger.info("Ordered  Successfully " + order.getOrderId());
		});
		return "Thanks for Ordering ,Your Order Placed Successfully !";
	}

	@Override
	public String cancelOrder(Long orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			String orderStatus = order.get().getOrderStatus();
			if ("CANCELLED".equals(orderStatus)) {
				return "Order already Cancelled";
			} else {
				order.get().setOrderStatus("CANCELLED");
				orderRepository.save(order.get());
			}
		}
		return "Order Cancelled Successfully";
	}

	@Override
	public String updateOrder(Long orderId, OrderRequestDto orderUpdateDto) {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			if (order.get().getRestaurantId() != orderUpdateDto.getRestaurantId()) {
				return "Cannot change restaurant while updating order";
			}
			order.get().setRestaurantId(orderUpdateDto.getRestaurantId());
			order.get().setCustomerId(orderUpdateDto.getCustomerId());
			order.get().setOrderStatus("UPDATED");
			orderRepository.save(order.get());
			CompletableFuture<List<MenuItemResponseDto>> menuItemsList = CompletableFuture.supplyAsync(() -> {
				return webClientBuilder
						.build()
						.get()
						.uri(ENDPOINT_URL + orderUpdateDto.getRestaurantId())
						.retrieve()
						.bodyToMono(new ParameterizedTypeReference<List<MenuItemResponseDto>>() {})
						.block();
			}, executorService);
			List<OrderedItemRequestDto> itemDto = orderUpdateDto.getItems();
			List<OrderedItem> existingItems = orderItemRepository.findByOrderOrderId(order.get().getOrderId());
			itemDto.forEach(item -> {
				existingItems.forEach(existingItem -> {
					if (item.getItemId() == existingItem.getItemId()) {
						existingItem.setQuantity(item.getQuantity());
						menuItemsList.thenAccept(menuItems -> {
							menuItems.forEach(menuItem -> {
								if (menuItem.getMenuItemId() == item.getItemId()) {
									existingItem.setTotalPrice(existingItem.getQuantity() * menuItem.getPrice());
								}
							});
						}).thenRun(() -> {
							logger.info("Order Updated Successfully " + order.get().getOrderId());
						});
						orderItemRepository.save(existingItem);
					}
				});
			});
		}
		return "Order Updated Successfully";
	}

	@Override
	public Mono<Double> getOrderAmount(Long orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
		double[] totalPrice = { 0 };
		if (order.isPresent()) {
			List<OrderedItem> orderedItems = orderItemRepository.findByOrderOrderId(order.get().getOrderId());
			orderedItems.forEach(item -> {
				totalPrice[0] += item.getTotalPrice();
			});
		}
		return Mono.just(totalPrice[0]);
	}

	@Override
	public List<OrderedItemResponse> getOrderDetails(Long orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
		List<OrderedItemResponse> itemResponse = new ArrayList<OrderedItemResponse>();
		if (order.isPresent()) {
			List<OrderedItem> orderedItems = orderItemRepository.findByOrderOrderId(order.get().getOrderId());
			orderedItems.forEach(item -> {
				OrderedItemResponse response = new OrderedItemResponse();
				response.setOrderedItemId(item.getOrderedItemId());
				response.setItemName(item.getItemName());
				response.setQuantity(item.getQuantity());
				response.setTotalPrice(item.getTotalPrice());
				itemResponse.add(response);
			});
		}
		return itemResponse;
	}

	@Override
	public String deleteOrder(Long orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			orderRepository.deleteById(order.get().getOrderId());
		}
		return "Order Deleted Successfully";
	}
}
