package com.project.code.Service;


import com.project.code.Model.Inventory;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;
    
// 1. **validateInventory Method**:
//    - Checks if an inventory record exists for a given product and store combination.
//    - Parameters: `Inventory inventory`
//    - Return Type: `boolean` (Returns `false` if inventory exists, otherwise `true`)
    public boolean validateInventory(Inventory inventory) {
        if (inventory == null || inventory.getProduct() == null || inventory.getStore() == null) {
            return false; // Prevent NullPointerException
        }
        return !inventoryRepository.existsByProductIdAndStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
    }
// 2. **validateProduct Method**:
//    - Checks if a product exists by its name.
//    - Parameters: `Product product`
//    - Return Type: `boolean` (Returns `false` if a product with the same name exists, otherwise `true`)
        public boolean isProductNameUnique(String productName) {
            return !productRepository.existsByName(productName);
        }
// 3. **ValidateProductId Method**:
//    - Checks if a product exists by its ID.
//    - Parameters: `long id`
//    - Return Type: `boolean` (Returns `false` if the product does not exist with the given ID, otherwise `true`)
    public boolean validateProductId(long id) {
        return productRepository.existsById(id);
    }
// 4. **getInventoryId Method**:
//    - Fetches the inventory record for a given product and store combination.
//    - Parameters: `Inventory inventory`
//    - Return Type: `Inventory` (Returns the inventory record for the product-store combination)
    public Inventory getInventoryId(Inventory inventory) {
        if (inventory == null || inventory.getProduct() == null || inventory.getStore() == null) {
            return null;
        }
        return inventoryRepository.findByProductIdAndStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
    }
}
