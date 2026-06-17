package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that this is a REST controller, which handles HTTP requests and responses.
//    - Use `@RequestMapping("/inventory")` to set the base URL path for all methods in this controller. All endpoints related to inventory will be prefixed with `/inventory`.


    // 2. Autowired Dependencies:
//    - Autowire necessary repositories and services:
//      - `ProductRepository` will be used to interact with product data (i.e., finding, updating products).
//      - `InventoryRepository` will handle CRUD operations related to the inventory.
//      - `ServiceClass` will help with the validation logic (e.g., validating product IDs and inventory data).
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ServiceClass serviceClass;

    // 3. Define the `updateInventory` Method:
//    - This method handles HTTP PUT requests to update inventory for a product.
//    - It takes a `CombinedRequest` (containing `Product` and `Inventory`) in the request body.
//    - The product ID is validated, and if valid, the inventory is updated in the database.
//    - If the inventory exists, update it and return a success message. If not, return a message indicating no data available.
    @PutMapping("updateInventory")
    public ResponseEntity<Map<String, String>> updateInventory(
            @RequestBody CombinedRequest request
    ) {
        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        if (
                product != null &&
                product.getName() != null &&
                serviceClass.isProductNameUnique(product.getName())
        ) {
            inventory.setProduct(product);
            inventoryRepository.save(inventory);
            return ResponseEntity.ok(Map.of("Message", "product updated successfully"));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("Error", "product is invalid"));
    }

    // 4. Define the `saveInventory` Method:
//    - This method handles HTTP POST requests to save a new inventory entry.
//    - It accepts an `Inventory` object in the request body.
//    - It first validates whether the inventory already exists. If it exists, it returns a message stating so. If it doesn’t exist, it saves the inventory and returns a success message.
    @PostMapping
    public ResponseEntity<Map<String, String>> saveInventory(@RequestBody Inventory inventory) {
        if (inventoryRepository.findById(inventory.getId()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "inventory existed"));
        }
        inventoryRepository.save(inventory);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "inventory saved successfully"));
    }

    // 5. Define the `getAllProducts` Method:
//    - This method handles HTTP GET requests to retrieve products for a specific store.
//    - It uses the `storeId` as a path variable and fetches the list of products from the database for the given store.
//    - The products are returned in a `Map` with the key `"products"`.
    @GetMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @PathVariable long storeId
    ) {
        Map<String, Object> map = new HashMap<>();
        List<Product> result = productRepository.findProductsByStoreId(storeId);
        map.put("products", result);
        System.out.println(map);
        return ResponseEntity.ok(
                Map.of("products", map)
        );
    }

// 6. Define the `getProductName` Method:
//    - This method handles HTTP GET requests to filter products by category and name.
//    - If either the category or name is `"null"`, adjust the filtering logic accordingly.
//    - Return the filtered products in the response with the key `"product"`.
    @GetMapping("filter/{category}/{name}/{storeid}")
    public ResponseEntity<Map<String, Object>> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable long storeId
    ) {
        // 1. Fetch all products from your repository
        List<Product> allProducts = productRepository.findAll();

        // 2. Stream and apply dynamic filtering logic
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> {
                    // If category is provided (and not the literal string "null"), it must match
                    boolean matchesCategory = (category == null || category.equals("null"))
                            || product.getCategory().equals(category);

                    // If name is provided (and not the literal string "null"), it must match
                    boolean matchesName = (name == null || name.equals("null"))
                            || product.getName().equalsIgnoreCase(name);

                    // Keep the product only if both conditions pass
                    return matchesCategory && matchesName;
                })
                .toList();

        // 3. Return the list wrapped in a HashMap using the key "product"
        Map<String, Object> response = new HashMap<>();
        response.put("product", filteredProducts);
        return ResponseEntity.ok(response);
    }

// 7. Define the `searchProduct` Method:
//    - This method handles HTTP GET requests to search for products by name within a specific store.
//    - It uses `name` and `storeId` as parameters and searches for products that match the `name` in the specified store.
//    - The search results are returned in the response with the key `"product"`.
    @GetMapping("searchProduct")
    public  ResponseEntity<Map<String, Object>> searchProduct(
            @RequestParam String name,
            @RequestParam long storeId
    ) {
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);

        Product filteredProduct = inventories.stream()
                .filter(inventory -> inventory.getProduct().getName().equals(name))
                .map(Inventory::getProduct)
                .findFirst()
                .orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("product", filteredProduct);
        return ResponseEntity.ok(response);
    }

// 8. Define the `removeProduct` Method:
//    - This method handles HTTP DELETE requests to delete a product by its ID.
//    - It first validates if the product exists. If it does, it deletes the product from the `ProductRepository` and also removes the related inventory entry from the `InventoryRepository`.
//    - Returns a success message with the key `"message"` indicating successful deletion.
    @DeleteMapping("removeProduct/{id}")
    public ResponseEntity<Map<String, String>> removeProduct(@PathVariable long id) {
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity
                    .ok(Map.of("message", "product deleted successfully"));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("Error", "product does not exist"));
    }

// 9. Define the `validateQuantity` Method:
//    - This method handles HTTP GET requests to validate if a specified quantity of a product is available in stock for a given store.
//    - It checks the inventory for the product in the specified store and compares it to the requested quantity.
//    - If sufficient stock is available, return `true`; otherwise, return `false`.
    @GetMapping("validateQuantity")
    public ResponseEntity<Map<String, Boolean>> validateQuantity(
            @RequestParam long productId,
            @RequestParam long storeId,
            @RequestParam int quantity
    ) {

        Inventory matchingInventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId);
        // 3. If the product isn't found in that store, they have 0 stock (insufficient)
        if (matchingInventory == null) {
            return ResponseEntity.ok(Map.of("isAvailable", false));
        }
        // 4. Compare available stock to the requested quantity
        // Assuming your Inventory entity has a getQuantity() or getStock() method:
        boolean hasEnoughStock = matchingInventory.getStockLevel() >= quantity;
        return ResponseEntity.ok(Map.of("isAvailable", hasEnoughStock));
    }
}
