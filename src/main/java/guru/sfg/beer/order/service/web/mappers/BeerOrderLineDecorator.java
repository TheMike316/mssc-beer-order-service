package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.services.beer.BeerService;
import guru.sfg.beer.order.service.web.model.BeerOrderLineDto;
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

        var beer = beerService.getBeer(line.getBeerId());

        dto.setBeerName(beer.getBeerName());
        dto.setBeerStyle(beer.getBeerStyle());

        return dto;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        return delegate.dtoToBeerOrderLine(dto);
    }
}
