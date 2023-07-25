package com.programmingtechie.productservice;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.assertions.Assertions;
import com.programmingtechie.productservice.dto.ProductRequest;
import com.programmingtechie.productservice.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;
	
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	@Order(1)
	@DisplayName("Test if a Product can be created and stored - shouldCreateProduct")
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
			.andExpect(MockMvcResultMatchers.status().isCreated());
		
		Assertions.assertTrue(productRepository.findAll().size() == 1);

		log.info("There is/are {} product/s in the MongoDB.",productRepository.findAll().size());
	}
	
	@Test
	@Order(2)
	@DisplayName("Test if Products can be retrieved - shouldGetAllProduct")
	void shouldGetAllProduct() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andReturn();
		
		String content = result.getResponse().getContentAsString();
		
		log.info("Retrieved Product: {}", content);
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 14")
				.description("iphone 14")
				.price(BigDecimal.valueOf(1200))
				.build();
		
	}

}
