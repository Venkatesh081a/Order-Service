package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordered_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderedItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderedItemId;
	private String itemName;
	private int quantity;
	private double totalPrice;
	private Long itemId;
	@ManyToOne
	@JoinColumn(name = "orderId_fk", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Order order;

	public OrderedItem(String itemName, int quantity, double totalPrice, Long itemId, Order order) {
		super();
		this.itemName = itemName;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.itemId = itemId;
		this.order = order;
	}

}
