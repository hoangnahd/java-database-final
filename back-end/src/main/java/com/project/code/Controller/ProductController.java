package com.project.code.Controller;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to designate it as a REST controller for handling HTTP requests.
//    - Map the class to the `/product` URL using `@RequestMapping("/product")`.


// 2. Autowired Dependencies:
//    - Inject the following dependencies via `@Autowired`:
//        - `ProductRepository` for CRUD operations on products.
//        - `ServiceClass` for product validation and business logic.
//        - `InventoryRepository` for managing the inventory linked to products.
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ServiceClass serviceClass;
    @Autowired
    private InventoryRepository inventoryRepository;

// 3. Define the `addProduct` Method:
//    - Annotate with `@PostMapping` to handle POST requests for adding a new product.
//    - Accept `Product` object in the request body.
//    - Validate product existence using `validateProduct()` in `ServiceClass`.
//    - Save the valid product using `save()` method of `ProductRepository`.
//    - Catch exceptions (e.g., `DataIntegrityViolationException`) and return appropriate error message.
    @PostMapping("addProduct")
    public ResponseEntity<Map<String, String>> addProduct(@RequestBody Product product) {
        try {
            if (
                    product != null &&
                            product.getName() != null &&
                            serviceClass.isProductNameUnique(product.getName())
            ) {
                productRepository.save(product);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Map.of("Message", "Product saved successfully"));
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Message", "Product is not valid"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Catch specific database integrity issues (e.g., duplicate keys, null constraints)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Database violation: " + e.getMostSpecificCause().getMessage()));

        } catch (Exception e) {
            // Catch-all for any other unexpected runtime exceptions
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

// 4. Define the `getProductbyId` Method:
//    - Annotate with `@GetMapping("/product/{id}")` to handle GET requests for retrieving a product by ID.
//    - Accept product ID via `@PathVariable`.
//    - Use `findById(id)` method from `ProductRepository` to fetch the product.
//    - Return the product in a `Map<String, Object>` with key `products`.
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        return Map.of("products", product);
    }

 // 5. Define the `updateProduct` Method:
//    - Annotate with `@PutMapping` to handle PUT requests for updating an existing product.
//    - Accept updated `Product` object in the request body.
//    - Use `save()` method from `ProductRepository` to update the product.
//    - Return a success message with key `message` after updating the product.
    @PutMapping("/updateProduct")
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product) {
        // Optional Best Practice: Ensure the product actually exists before updating
        if (product.getId() == null || !productRepository.existsById(product.getId())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found with the given ID"));
        }
        productRepository.save(product);
        return ResponseEntity
                .ok(Map.of("Message", "Product updated successfully"));
    }

// 6. Define the `filterbyCategoryProduct` Method:
//    - Annotate with `@GetMapping("/category/{name}/{category}")` to handle GET requests for filtering products by `name` and `category`.
//    - Use conditional filtering logic if `name` or `category` is `"null"`.
//    - Fetch products based on category using methods like `findByCategory()` or `findProductBySubNameAndCategory()`.
//    - Return filtered products in a `Map<String, Object>` with key `products`.
    @GetMapping("/category/{name}/{category}")
    public ResponseEntity<Map<String, Object>> filterbyCategoryProduct(
            @PathVariable String name,
            @PathVariable String category
    ) {
        // 1. Filter the stream and collect the matches into a List
        List<Product> productList = productRepository.findByCategory(category)
                .stream()
                .filter(product -> product.getName().equals(name))
                .collect(Collectors.toList()); // Collects all matches into a List<Product>

        // 2. Put the list into your response map
        Map<String, Object> response = new HashMap<>();
        response.put("products", productList);

        return ResponseEntity
                .ok(response);
    }

 // 7. Define the `listProduct` Method:
//    - Annotate with `@GetMapping` to handle GET requests to fetch all products.
//    - Fetch all products using `findAll()` method from `ProductRepository`.
//    - Return all products in a `Map<String, Object>` with key `products`.
    @GetMapping("listProduct")
    public ResponseEntity<Map<String, Object>> listProduct() {
        return ResponseEntity
                .ok(Map.of("Products", productRepository.findAll()));
    }

// 8. Define the `getProductbyCategoryAndStoreId` Method:
//    - Annotate with `@GetMapping("filter/{category}/{storeid}")` to filter products by `category` and `storeId`.
//    - Use `findProductByCategory()` method from `ProductRepository` to retrieve products.
//    - Return filtered products in a `Map<String, Object>` with key `product`.
    @GetMapping("filter/{category}/{storeId}")
    public ResponseEntity<Map<String, Object>> getProductbyCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable long storeId
    ) {
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);

        Product product = inventories.stream()
                .filter(inventory -> inventory.getProduct().getCategory().equals(category))
                .map(Inventory::getProduct)
                .findFirst()
                .orElse(null);

        // FIX: Use HashMap instead of Map.of() to safely handle nulls
        Map<String, Object> response = new HashMap<>();
        response.put("product", product);

        return ResponseEntity.ok(response);
    }

// 9. Define the `deleteProduct` Method:
//    - Annotate with `@DeleteMapping("/{id}")` to handle DELETE requests for removing a product by its ID.
//    - Validate product existence using `ValidateProductId()` in `ServiceClass`.
//    - Remove product from `Inventory` first using `deleteByProductId(id)` in `InventoryRepository`.
//    - Remove product from `Product` using `deleteById(id)` in `ProductRepository`.
//    - Return a success message with key `message` indicating product deletion.
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteByProductId(@PathVariable long id) {
        // Optional Best Practice: Ensure the product actually exists before updating
        if (!productRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found with the given ID"));
        }
        productRepository.deleteById(id);
        return ResponseEntity
                .ok(Map.of("Message", "Product deleted successfully"));
    }

 // 10. Define the `searchProduct` Method:
//    - Annotate with `@GetMapping("/searchProduct/{name}")` to search for products by `name`.
//    - Use `findProductBySubName()` method from `ProductRepository` to search products by name.
//    - Return search results in a `Map<String, Object>` with key `products`.
    @GetMapping("/searchProduct/{name}")
    public ResponseEntity<Map<String, Object>> findProductBySubName(@PathVariable String name) {
        return ResponseEntity
                .ok(Map.of("products", productRepository.findByName(name)));
    }

}
