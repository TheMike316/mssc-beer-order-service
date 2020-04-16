package org.brewery.beerorderservice.services;

import org.brewery.beerorderservice.domain.BeerOrder;

public interface BeerOrderManagerService {

    String BEER_ORDER_ID_HEADER = "beer-order-id";

    BeerOrder newBeerOrder(BeerOrder beerOrder);

}
