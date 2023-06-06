package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
