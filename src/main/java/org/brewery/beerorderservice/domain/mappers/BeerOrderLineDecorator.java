package org.brewery.beerorderservice.domain.mappers;

import org.brewery.beerorderservice.domain.BeerOrderLine;
import org.brewery.beerorderservice.services.beer.BeerService;
import org.brewery.common.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BeerOrderLineDecorator implements BeerOrderLineMapper {

    private BeerService beerService;
    private BeerOrderLineMapper delegate;

    @Autowired
    public void setBeerService(BeerService beerService) {
        this.beerService = beerService;
    }

    @Autowired
    public void setDelegate(@Qualifier("delegate") BeerOrderLineMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        var dto = delegate.beerOrderLineToDto(line);

        var beer = beerService.getBeerByUpc(line.getUpc());

        beer.ifPresent(b -> {
            dto.setBeerId(b.getId());
            dto.setBeerName(b.getBeerName());
            dto.setBeerStyle(b.getBeerStyle());
            dto.setPrice(b.getPrice());
        });

        return dto;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        return delegate.dtoToBeerOrderLine(dto);
    }
}
