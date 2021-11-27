package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponseDto {

	private Long menuId;
	private String activeFrom;
	private String activeTill;
	private RestaurantResponseDto restaurant;
}
