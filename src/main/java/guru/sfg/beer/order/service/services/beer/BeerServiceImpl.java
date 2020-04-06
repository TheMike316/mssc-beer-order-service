package guru.sfg.beer.order.service.services.beer;

import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public class BeerServiceImpl implements BeerService {

    private static final String BEER_API_PATH = "/api/v1/beer/{beerId}";

    private final RestTemplate restTemplate;
    private final String beerApiAddress;

    public BeerServiceImpl(RestTemplateBuilder builder,
                           @Value("${beer-api.address}") String beerApiAddress) {
        this.restTemplate = builder.build();
        this.beerApiAddress = beerApiAddress;
    }

    @Override
    public BeerDto getBeer(UUID beerId) {
        var uri = UriComponentsBuilder.fromUriString(beerApiAddress + BEER_API_PATH).build(beerId);
        return restTemplate.getForObject(uri, BeerDto.class);
    }
}
