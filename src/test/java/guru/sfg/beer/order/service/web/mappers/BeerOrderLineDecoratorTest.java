package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.services.beer.BeerService;
import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import guru.sfg.beer.order.service.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DateMapper.class, BeerOrderLineMapperImpl.class, BeerOrderLineMapperImpl_.class})
class BeerOrderLineDecoratorTest {

    @MockBean
    BeerService beerService;

    @SpyBean
    BeerOrderLineMapper beerOrderLineMapper;

    @Autowired
    BeerOrderLineDecorator decorator;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void beerOrderLineToDto() {
        UUID beerId = UUID.randomUUID();
        BeerOrderLine line = BeerOrderLine.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .build();

        BeerDto beer = BeerDto.builder()
                .id(beerId)
                .upc("13412341234")
                .beerName("Testy McTest")
                .beerStyle("ALE")
                .price(new BigDecimal("10.95"))
                .build();
        given(beerService.getBeerByUpc(any())).willReturn(Optional.of(beer));

        var actual = decorator.beerOrderLineToDto(line);

        assertNotNull(actual);
        assertEquals(line.getId(), actual.getId());
        assertEquals(line.getBeerId(), actual.getBeerId());
        assertEquals(beer.getId(), actual.getBeerId());
        assertEquals(beer.getBeerName(), actual.getBeerName());
        assertEquals(beer.getBeerStyle(), actual.getBeerStyle());

        verify(beerOrderLineMapper, times(1)).beerOrderLineToDto(any());
        verify(beerService, times(1)).getBeerByUpc(any());
    }

    @Test
    void dtoToBeerOrderLine() {
        decorator.dtoToBeerOrderLine(mock(BeerOrderLineDto.class));

        verify(beerOrderLineMapper, times(1)).dtoToBeerOrderLine(any());
        verify(beerService, times(0)).getBeerById(any());
    }
}