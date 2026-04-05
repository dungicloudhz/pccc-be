package com.cozyquoteforge.pccc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionDetailDto {
    private UUID id;

    @JsonProperty("projectName")
    private String projectName;

    @JsonProperty("materialPercent")
    private BigDecimal materialPercent;

    @JsonProperty("laborPercent")
    private BigDecimal laborPercent;

    private List<WorkshopDto> workshops;
    private List<SectionDto> sections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkshopDto {
        private UUID id;

        private Integer orderId;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkshopValueDto {
        private UUID workshopId;
        private String tenXuong;
        private BigDecimal value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionDto {
        private UUID id;
        private Integer orderId;
        private String name;
        private List<RowDto> rows;
        @JsonProperty("id_sections")
        private String idSections;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RowDto {
        private String id; // Now productId
        private Integer orderId;
        private Long productId; // Now productId

        private String code;
        private String note;

        @JsonProperty("totalCable")
        private BigDecimal totalCable;

        @JsonProperty("lossPercent")
        private BigDecimal lossPercent;

        @JsonProperty("materialPrice")
        private BigDecimal materialPrice;

        @JsonProperty("laborPrice")
        private BigDecimal laborPrice;

        @JsonProperty("workshopValues")
        private List<WorkshopValueDto> workshopValues;

    }
}
