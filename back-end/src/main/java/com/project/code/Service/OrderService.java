package com.project.code.Service;


import com.project.code.Model.*;
import com.project.code.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;

// 1. **saveOrder Method**:
//    - Processes a customer's order, including saving the order details and associated items.
//    - Parameters: `PlaceOrderRequestDTO placeOrderRequest` (Request data for placing an order)
//    - Return Type: `void` (This method doesn't return anything, it just processes the order)
    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequestDTO) {
        Customer customer = customerRepository
                                    .findByEmail(placeOrderRequestDTO
                                    .getCustomerEmail());
        // 2. Fail-Fast: If they don't exist, throw an exception immediately
        if (customer == null) {
            throw new IllegalArgumentException(
                    "Order placement failed: No customer account found with email: " +
                            placeOrderRequestDTO.getCustomerEmail()
            );
        }

        Store store = storeRepository.findById(placeOrderRequestDTO.getStoreId())
                .orElseThrow(() -> new RuntimeException(
                        "Store not found with ID: " + placeOrderRequestDTO.getStoreId()
                ));

        OrderDetails orderDetails = orderDetailsRepository.save(new OrderDetails(
                customer,
                store,
                placeOrderRequestDTO.getTotalPrice(),
                LocalDateTime.parse(placeOrderRequestDTO.getDatetime())
        ));
        createAndSaveOrderItems(
                placeOrderRequestDTO.getPurchaseProduct(),
                orderDetails
        );

    }

// 2. **Retrieve or Create the Customer**:
//    - Check if the customer exists by their email using `findByEmail`.
//    - If the customer exists, use the existing customer; otherwise, create and save a new customer using `customerRepository.save()`.
    public Customer getOrCreateCustomer(Customer customer) {
        if(customerRepository.findByEmail(customer.getEmail()) != null) {
            return customer;
        }
        return customerRepository.save(customer);
    }
// 3. **Retrieve the Store**:
//    - Fetch the store by ID from `storeRepository`.
//    - If the store doesn't exist, throw an exception. Use `storeRepository.findById()`.
    public Store findStoreById(long id) {
        return storeRepository.findById(id).orElseThrow(() -> new RuntimeException("Store does not exist"));
    }
// 4. **Create OrderDetails**:
//    - Create a new `OrderDetails` object and set customer, store, total price, and the current timestamp.
//    - Set the order date using `java.time.LocalDateTime.now()` and save the order with `orderDetailsRepository.save()`.
    public void createDetails(PlaceOrderRequestDTO placeOrderRequestDTO) {
        Customer customer = customerRepository.findByEmail(placeOrderRequestDTO.getCustomerEmail());

        Store store = storeRepository.findById(placeOrderRequestDTO.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + placeOrderRequestDTO.getStoreId()));
        OrderDetails orderDetails = new OrderDetails(
                customer,
                store,
                placeOrderRequestDTO.getTotalPrice(),
                LocalDateTime.now()
        );
        orderDetailsRepository.save(orderDetails);
    }
// 5. **Create and Save OrderItems**:
//    - For each product purchased, find the corresponding inventory, update stock levels, and save the changes using `inventoryRepository.save()`.
//    - Create and save `OrderItem` for each product and associate it with the `OrderDetails` using `orderItemRepository.save()`.
    private void createAndSaveOrderItems(
            List<PurchaseProductDTO> items,
            OrderDetails orderDetails ) {

        // Update phase
        List<OrderItem> orderItemEntities = new ArrayList<>();
        // Validation phase
        for (PurchaseProductDTO item : items) {

            Inventory inventory =
                    inventoryRepository.findByProductIdAndStoreId(
                            item.getId(),
                            orderDetails.getStore().getId());

            if (inventory == null) {
                throw new RuntimeException(
                        "Inventory not found for product "
                                + item.getId());
            }

            if (inventory.getStockLevel() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product "
                                + item.getId());
            }
            inventory.setStockLevel(
                    inventory.getStockLevel() - item.getQuantity());

            inventoryRepository.save(inventory);

            Product product = productRepository.findById(item.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            orderItemEntities.add(
                    new OrderItem(
                            orderDetails,
                            product,
                            item.getQuantity(),
                            item.getPrice()
                    )
            );
        }
        orderItemRepository.saveAll(orderItemEntities);
    }
   
}
