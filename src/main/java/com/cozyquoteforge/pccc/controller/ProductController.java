package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.ProductDto;
import com.cozyquoteforge.pccc.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EDITOR','ROLE_ADMIN')")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_EDITOR','ROLE_ADMIN')")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EDITOR','ROLE_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('ROLE_EDITOR','ROLE_ADMIN')")
    public ResponseEntity<List<ProductDto>> createProducts(@Valid @RequestBody List<@Valid ProductDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProducts(dtos));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EDITOR','ROLE_ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EDITOR','ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
