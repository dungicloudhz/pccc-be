package com.cozyquoteforge.pccc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "construction_workshops")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionWorkshop {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id", nullable = false)
    @ToString.Exclude
    private Construction construction;

    @Column(nullable = false)
    private String name;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "display_order")
    private Integer displayOrder;
}
