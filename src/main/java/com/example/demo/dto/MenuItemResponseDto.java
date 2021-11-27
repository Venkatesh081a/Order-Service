package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemResponseDto {

	private Long menuItemId;
	private String menuItemName;
	private String description;
	private int price;
}
