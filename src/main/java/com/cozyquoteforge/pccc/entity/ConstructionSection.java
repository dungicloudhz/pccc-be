package com.cozyquoteforge.pccc.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Entity
@Table(name = "construction_sections")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionSection {
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

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String rows = "[]";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstructionSection)) return false;
        ConstructionSection other = (ConstructionSection) o;
        if (id == null || other.id == null) return false;
        return id.equals(other.id);
    }
}
