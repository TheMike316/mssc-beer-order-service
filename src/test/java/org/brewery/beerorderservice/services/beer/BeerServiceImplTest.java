package org.brewery.beerorderservice.services.beer;

import org.brewery.common.model.BeerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BeerServiceImplTest {

    @Mock
    RestTemplateBuilder builder;

    @Mock
    RestTemplate restTemplate;

    String apiAddress = "http://testhost:8080";

    BeerServiceImpl service;

    UUID id = UUID.randomUUID();
    BeerDto dto = BeerDto.builder()
            .id(id)
            .beerName("Stiegl")
            .beerStyle("LAGER")
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        given(builder.build()).willReturn(restTemplate);

        service = new BeerServiceImpl(builder, apiAddress);
    }

    @Test
    void getBeerById() {
        given(restTemplate.getForObject(any(URI.class), eq(BeerDto.class))).willReturn(dto);

        var actual = service.getBeerById(id).get();

        Assertions.assertEquals(dto, actual);
        verify(restTemplate, times(1)).getForObject(any(URI.class), eq(BeerDto.class));
    }

    @Test
    void getBeerByUpc() {
        given(restTemplate.getForObject(any(URI.class), eq(BeerDto.class))).willReturn(dto);

        var actual = service.getBeerByUpc("0664554136").get();

        Assertions.assertEquals(dto, actual);
        verify(restTemplate, times(1)).getForObject(any(URI.class), eq(BeerDto.class));
    }
}