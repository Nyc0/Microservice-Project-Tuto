package com.programmingtechie.orderservice.service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.programmingtechie.orderservice.dto.InventoryResponse;
import com.programmingtechie.orderservice.dto.OrderLineItemsDto;
import com.programmingtechie.orderservice.dto.OrderRequest;
import com.programmingtechie.orderservice.model.Order;
import com.programmingtechie.orderservice.model.OrderLineItems;
import com.programmingtechie.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final WebClient webClient;
	
	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
			.stream()
			.map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
			.toList();
		
		order.setOrderLineItemsList(orderLineItems);
		
		List<String> skuCodes = order.getOrderLineItemsList().stream()
				.map(orderLineItem -> orderLineItem.getSkuCode())
				.toList();
		
		// Call inventory service, and place order if the product is in stock
		InventoryResponse[] inventoryResponseArray = webClient.get()
				.uri("http://localhost:8082/api/inventory", 
						uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
				.retrieve()
				.bodyToMono(InventoryResponse[].class)
				.block();
		
		Boolean allProductInStock = Arrays.stream(inventoryResponseArray)
				.allMatch(inventoryResponse -> inventoryResponse.isInStock());

		if(allProductInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("Products are not in stock, please try again later.");
		}
		
		
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
}
