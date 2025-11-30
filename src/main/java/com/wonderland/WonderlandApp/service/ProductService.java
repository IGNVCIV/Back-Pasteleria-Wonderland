package com.wonderland.WonderlandApp.service;

//Importaciones del model y repository
import com.wonderland.WonderlandApp.model.Products;
import com.wonderland.WonderlandApp.repository.ProductRepository;
import com.wonderland.WonderlandApp.util.ProductIdGenerator;

//Importaicon para transaccion en BD
//import jakarta.transaction.Transactional;

//Importacion para dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Importaciones Java
import java.util.List;

@SuppressWarnings("null")
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Products> getByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Products> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Products> findAll() {
        return productRepository.findAll();
    }

    public Products findbyId(String id_products) {
        return productRepository.findById(id_products).get();
    }

    public int countByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    public Products save(Products product) {
        if (product.getId_products() == null || product.getId_products().isBlank()) {
            int count = productRepository.countByCategory(product.getCategory()) + 1;
            String newId = ProductIdGenerator.generateId(product.getCategory(), count);
            product.setId_products(newId);
        }
        return productRepository.save(product);
    }

    public void delete(String id_products) {
        productRepository.deleteById(id_products);
    }

    public boolean existeId(String id_products) {
        return productRepository.existsById(id_products);
    }

    public List<Products> findAllPrice() {
        List<Products> products = findAll();// oculta precios app
        return products;
    }

}
