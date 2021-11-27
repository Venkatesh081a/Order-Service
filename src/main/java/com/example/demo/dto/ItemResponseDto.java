package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

	private Long menuItemId;
	private String menuItemName;
	private String description;
	private int price;
	private MenuResponseDto menu;
}
