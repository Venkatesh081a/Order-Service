package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto {

	private Long restaurantId;
	private String restaurantName;
	private String location;
	private String cuisine;
	private int budget;
	private double rating;
}
