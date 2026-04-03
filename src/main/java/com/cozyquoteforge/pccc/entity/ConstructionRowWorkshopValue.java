package com.cozyquoteforge.pccc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "construction_row_workshop_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionRowWorkshopValue {
    @EmbeddedId
    private ConstructionRowWorkshopValueId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rowId")
    @JoinColumn(name = "row_id")
    @ToString.Exclude
    private ConstructionRow row;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workshopId")
    @JoinColumn(name = "workshop_id")
    @ToString.Exclude
    private ConstructionWorkshop workshop;

    @Column
    private BigDecimal value;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConstructionRowWorkshopValueId implements Serializable {
        @Column(name = "row_id")
        private UUID rowId;

        @Column(name = "workshop_id")
        private UUID workshopId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConstructionRowWorkshopValueId that = (ConstructionRowWorkshopValueId) o;
            return Objects.equals(rowId, that.rowId) && Objects.equals(workshopId, that.workshopId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rowId, workshopId);
        }
    }
}
