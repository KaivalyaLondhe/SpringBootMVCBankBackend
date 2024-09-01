package com.aurionpro.bank.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageResponse<T> {

	private int totalPages;
	private long totalElements;
	private int size;
	private List<T> content;
	private boolean isLastPage;
}
