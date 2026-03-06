package com.example.exercice13.service;

import com.example.exercice13.exception.InsufficientStockException;
import com.example.exercice13.model.Product;
import com.example.exercice13.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> createProduct(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    public Mono<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setStock(product.getStock());
                    return productRepository.save(existingProduct);
                });
    }

    public Mono<Void> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

    public Flux<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Mono<Product> buyProduct(Long id, Integer quantity) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    if (quantity <= 0) {
                        return Mono.error(new IllegalArgumentException("La quantité doit être supérieure à 0"));
                    }

                    if (product.getStock() < quantity) {
                        return Mono.error(new InsufficientStockException(
                                "Stock insuffisant. Stock disponible : " + product.getStock()
                        ));
                    }

                    product.setStock(product.getStock() - quantity);
                    return productRepository.save(product);
                });
    }
}