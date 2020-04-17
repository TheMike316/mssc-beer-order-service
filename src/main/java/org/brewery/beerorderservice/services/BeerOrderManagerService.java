package org.brewery.beerorderservice.services;

import org.brewery.beerorderservice.domain.BeerOrder;
import org.brewery.common.model.BeerOrderDto;

import java.util.UUID;

public interface BeerOrderManagerService {

    String BEER_ORDER_ID_HEADER = "beer-order-id";

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResponse(UUID beerOrderId, boolean valid);

    void processAllocationResponse(BeerOrderDto dto, boolean pendingInventory, boolean allocationError);
}
