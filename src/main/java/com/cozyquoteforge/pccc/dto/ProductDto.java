package com.cozyquoteforge.pccc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String unit;
    private String category;
    private String origin;
    private String code;

    @JsonProperty("materialUnitPrice")
    private BigDecimal materialUnitPrice;

    @JsonProperty("laborUnitPrice")
    private BigDecimal laborUnitPrice;

    @JsonProperty("lossPercent")
    private BigDecimal lossPercent;
}
