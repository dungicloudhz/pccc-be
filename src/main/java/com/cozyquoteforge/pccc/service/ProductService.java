package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.ProductDto;
import com.cozyquoteforge.pccc.entity.Product;
import com.cozyquoteforge.pccc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(String id) {
        return productRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public ProductDto createProduct(ProductDto dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .unit(dto.getUnit())
                .category(dto.getCategory())
                .origin(dto.getOrigin())
                .code(dto.getCode())
                .materialUnitPrice(dto.getMaterialUnitPrice())
                .laborUnitPrice(dto.getLaborUnitPrice())
                .lossPercent(dto.getLossPercent())
                .build();
        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    public List<ProductDto> createProducts(List<ProductDto> dtos) {
        List<Product> products = dtos.stream()
                .map(dto -> Product.builder()
                        .name(dto.getName())
                        .unit(dto.getUnit())
                        .category(dto.getCategory())
                        .origin(dto.getOrigin())
                        .code(dto.getCode())
                        .materialUnitPrice(dto.getMaterialUnitPrice())
                        .laborUnitPrice(dto.getLaborUnitPrice())
                        .lossPercent(dto.getLossPercent())
                        .build())
                .collect(Collectors.toList());
        List<Product> saved = productRepository.saveAll(products);
        return saved.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ProductDto updateProduct(String id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setName(dto.getName());
        product.setUnit(dto.getUnit());
        product.setCategory(dto.getCategory());
        product.setOrigin(dto.getOrigin());
        product.setCode(dto.getCode());
        product.setMaterialUnitPrice(dto.getMaterialUnitPrice());
        product.setLaborUnitPrice(dto.getLaborUnitPrice());
        product.setLossPercent(dto.getLossPercent());
        Product updated = productRepository.save(product);
        return toDto(updated);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .unit(product.getUnit())
                .category(product.getCategory())
                .origin(product.getOrigin())
                .code(product.getCode())
                .materialUnitPrice(product.getMaterialUnitPrice())
                .laborUnitPrice(product.getLaborUnitPrice())
                .lossPercent(product.getLossPercent())
                .build();
    }
}
