package org.brewery.beerorderservice.services;

import org.brewery.beerorderservice.domain.BeerOrder;

public interface BeerOrderManagerService {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

}
