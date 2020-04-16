package org.brewery.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerDto {

    private UUID id;
    private String upc;
    private String beerName;
    private String beerStyle;
    private BigDecimal price;

}
