package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByStore(String storeId) {
        return productRepository.findByStoreId(storeId);
    }
}
