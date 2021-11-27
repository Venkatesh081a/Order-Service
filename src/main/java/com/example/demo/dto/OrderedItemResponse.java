package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderedItemResponse {

	private Long orderedItemId;
	private String itemName;
	private int quantity;
	private double totalPrice;
}
