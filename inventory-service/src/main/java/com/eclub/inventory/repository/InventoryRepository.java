package com.eclub.inventory.repository;

import com.eclub.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.skuCode IN :skuCodes")
    List<Inventory> findByCodeSkuIn(@Param("skuCodes") List<String> skuCodes);
}
//    List<Inventory> findByCodeSkuIn(List<String> skuCode);
