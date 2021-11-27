package com.example.demo.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;
	private String orderStatus;
	private Long customerId;
	private Long restaurantId;

	@CreationTimestamp
	private LocalDateTime createdDateTime;
	@UpdateTimestamp
	private LocalDateTime updatedDateTime;

	public Order(String orderStatus, Long customerId, Long restaurantId) {
		super();
		this.orderStatus = orderStatus;
		this.customerId = customerId;
		this.restaurantId = restaurantId;
	}

}
