package com.cozyquoteforge.pccc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String unit;

    @Column
    private String category;

    @Column
    private String origin;

    @Column
    private String code;

    @Column(name = "material_unit_price")
    private BigDecimal materialUnitPrice;

    @Column(name = "labor_unit_price")
    private BigDecimal laborUnitPrice;

    @Column(name = "loss_percent")
    private BigDecimal lossPercent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
