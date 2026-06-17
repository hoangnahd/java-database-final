package com.project.code.Repo;


import com.project.code.Model.Inventory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

//    - **findByProductIdandStoreId**:
    public Inventory findByProductIdAndStoreId(Long productId, Long storeId);
    public boolean existsByProductIdAndStoreId(Long productId, Long storeId);
//    - **findByStore_Id**:
    public List<Inventory> findByStore_Id(Long storeId);
//    - **deleteByProductId**:
    @Modifying
    @Transactional
    public void deleteByProductId(Long productId);
}
