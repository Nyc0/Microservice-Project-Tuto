package com.programmingtechie.inventoryservice.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmingtechie.inventoryservice.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{

	List<Inventory> findBySkuCodeIn(List<String> skuCode);

}
