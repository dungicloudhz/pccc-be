package com.cozyquoteforge.pccc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "construction_rows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionRow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @ToString.Exclude
    private ConstructionSection section;

    @Column(name = "product_id")
    private String productId;

    @Column
    private String code;

    @Column
    private String note;

    @Column(name = "total_cable")
    private BigDecimal totalCable;

    @Column(name = "loss_percent")
    private BigDecimal lossPercent;

    @Column(name = "material_price")
    private BigDecimal materialPrice;

    @Column(name = "labor_price")
    private BigDecimal laborPrice;

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToMany(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<ConstructionRowWorkshopValue> workshopValues = new ArrayList<>();
}
