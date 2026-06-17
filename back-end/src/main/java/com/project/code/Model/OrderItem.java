package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
public class OrderItem {

// 1. Add 'id' field:
//    - Type: private Long
//    - This field will be auto-incremented.
//    - Use @Id to mark it as the primary key.
//    - Use @GeneratedValue(strategy = GenerationType.IDENTITY) to auto-increment it.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
// 2. Add 'order' field:
//    - Type: private OrderDetails
//    - This field refers to the order this item belongs to.
//    - Use @ManyToOne with @JoinColumn(name = "order_id") to define the foreign key relationship.
//    - Apply @JsonManagedReference to manage bidirectional relationships and JSON serialization.
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private OrderDetails order;
// 3. Add 'product' field:
//    - Type: private Product
//    - This field refers to the product in the order.
//    - Use @ManyToOne with @JoinColumn(name = "product_id") to define the foreign key relationship.
//    - Apply @JsonManagedReference to prevent circular references during JSON serialization.
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;
// 4. Add 'quantity' field:
//    - Type: private Integer
//    - This field represents the quantity of the product in the order.
    @Min(value = 0)
    private Integer quantity;

// 5. Add 'price' field:
//    - Type: private Double
//    - This field represents the price of the product at the time of the order.
    private Double price;
// 6. Add constructors:
//    - A no-argument constructor.
//    - A parameterized constructor that accepts OrderDetails, Product, quantity, and price as parameters.
// No-argument constructor (Required by JPA)
    public OrderItem() {
    }

    // Parameterized constructor
    // Note: Assuming 'OrderDetails' refers to the parent Order entity
    public OrderItem(OrderDetails order, Product product, Integer quantity, Double price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
// 7. Add @Entity annotation:
//    - Use @Entity above the class name to mark it as a JPA entity.

// 8. Add Getters and Setters:
//    - Add getter and setter methods for all fields (id, order, product, quantity, price).

    // ==========================================
    // Getters and Setters
    // ==========================================

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for order
    public OrderDetails getOrder() {
        return order;
    }

    public void setOrder(OrderDetails orderDetails) {
        this.order = orderDetails;
    }

    // Getter and Setter for product
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Getter and Setter for quantity
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for price
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}

