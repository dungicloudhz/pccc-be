package com.cozyquoteforge.pccc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectionDto {
        private UUID id;
        private String name;
        private List<RowDto> rows;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RowDto {
        private UUID id;

        @JsonProperty("productId")
        private String productId;

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
        private Map<UUID, BigDecimal> workshopValues;
    }
}
