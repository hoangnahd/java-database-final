package com.project.code.Repo;


import com.project.code.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

      //    - **findByEmail**:
      public Customer findByEmail(String email);

      // Find by name method
      public List<Customer> findByName(String name);
    
}


