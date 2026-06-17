package com.project.code.Controller;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to designate it as a REST controller for handling HTTP requests.
//    - Map the class to the `/store` URL using `@RequestMapping("/store")`.


 // 2. Autowired Dependencies:
//    - Inject the following dependencies via `@Autowired`:
//        - `StoreRepository` for managing store data.
//        - `OrderService` for handling order-related functionality.
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderService orderService;

 // 3. Define the `addStore` Method:
//    - Annotate with `@PostMapping` to create an endpoint for adding a new store.
//    - Accept `Store` object in the request body.
//    - Return a success message in a `Map<String, String>` with the key `message` containing store creation confirmation.
    @PostMapping("/addStore")
    public ResponseEntity<Map<String, String>> addStore(@RequestBody Store store) {
        storeRepository.save(store);
        return ResponseEntity
                .ok(Map.of("Message", "Store created successfully"));
    }

    // 4. Define the `validateStore` Method:
    //    - Annotate with `@GetMapping("validate/{storeId}")` to check if a store exists by its `storeId`.
    //    - Return a **boolean** indicating if the store exists.
     @GetMapping("/validate/{storeId}")
     public ResponseEntity<Map<String, Boolean>> validateStore(@PathVariable long storeId) {
         boolean exists = storeRepository.existsById(storeId);

         // Returns {"exists": true} or {"exists": false}
         return ResponseEntity.ok(Map.of("exists", exists));
     }

 // 5. Define the `placeOrder` Method:
//    - Annotate with `@PostMapping("/placeOrder")` to handle order placement.
//    - Accept `PlaceOrderRequestDTO` in the request body.
//    - Return a success message with key `message` if the order is successfully placed.
//    - Return an error message with key `Error` if there is an issue processing the order.
    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequestDTO) {
        try {
            orderService.saveOrder(placeOrderRequestDTO);
            return ResponseEntity
                    .ok(Map.of("Message", "Order successfully placed"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

}
