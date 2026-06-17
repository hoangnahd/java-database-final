package com.project.code.Repo;


import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    public List<Product> findByCategory(String category);
    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    public Product findBySku(String sku);
    public Product findByName(String name);
    public Product findByNameContainingAndCategory(String subName, String category);
    public boolean existsByName(String name);


    @Query("""
        select distinct i.product
        from Inventory i
        where i.store.id = :storeId
          and i.product.name like concat('%', :name, '%')
    """)
    public List<Product> findByNameLike(@Param("storeId") Long storeId,
                                 @Param("name") String name);

    @Query("""
        SELECT i.product FROM Inventory i 
            WHERE i.store.id = :storeId 
                AND LOWER(i.product.name) 
                    LIKE LOWER(CONCAT('%', :pname, '%')) 
                        AND i.product.category = :category
        """)
    public List<Product> findByNameAndCategory(long storeId, String pname,String category);

    @Query("""
            SELECT i.product FROM Inventory i 
                    WHERE i.store.id = :storeId 
                            AND i.product.category = :category
        """)
    public List<Product> findByCategoryAndStoreId(long storeId,String category);

    @Query("""
        SELECT i FROM Product i 
            WHERE LOWER(i.name) 
                LIKE LOWER(CONCAT('%', :pname, '%'))
    """)
    public List<Product> findProductBySubName(String pname);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    public List<Product> findProductsByStoreId(Long storeId);

    @Query("""
            SELECT i.product FROM Inventory i 
                    WHERE i.product.category = :category and i.store.id = :storeId
        """)
    public List<Product> findProductByCategory(String category, long storeId);

    @Query("""
            SELECT i FROM Product i 
                    WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) 
                            AND i.category = :category
        """)
    public List<Product> findProductBySubNameAndCategory(String pname, String category);
}
