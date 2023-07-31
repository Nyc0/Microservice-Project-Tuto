package com.programmingtechie.inventoryservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.programmingtechie.inventoryservice.Repository.InventoryRepository;
import com.programmingtechie.inventoryservice.model.Inventory;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> { 
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iPhone_14");
			inventory.setQuantity(100);
			
			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iPhone_14_red");
			inventory2.setQuantity(0);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory2);
		};
	}

}
