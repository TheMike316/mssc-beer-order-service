package org.brewery.beerorderservice.services;

import org.brewery.beerorderservice.domain.BeerOrder;

import java.util.UUID;

public interface BeerOrderManagerService {

    String BEER_ORDER_ID_HEADER = "beer-order-id";

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void sendValidationResponseEvent(UUID beerOrderId, boolean valid);

}
