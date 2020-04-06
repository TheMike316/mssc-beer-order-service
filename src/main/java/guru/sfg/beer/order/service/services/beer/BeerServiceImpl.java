package guru.sfg.beer.order.service.services.beer;

import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
public class BeerServiceImpl implements BeerService {

    private static final String BEER_API_PATH = "/api/v1/beer/{beerId}";
    private static final String BEER_UPC_API_PATH = "/api/v1/beer/upc/{upc}";

    private final RestTemplate restTemplate;
    private final String beerApiAddress;

    public BeerServiceImpl(RestTemplateBuilder builder,
                           @Value("${beer-api.address}") String beerApiAddress) {
        this.restTemplate = builder.build();
        this.beerApiAddress = beerApiAddress;
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID beerId) {
        var uri = UriComponentsBuilder.fromUriString(beerApiAddress + BEER_API_PATH).build(beerId);
        return callApi(uri);
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc) {
        var uri = UriComponentsBuilder.fromUriString(beerApiAddress + BEER_UPC_API_PATH).build(upc);
        return callApi(uri);
    }

    private Optional<BeerDto> callApi(URI uri) {
        return Optional.ofNullable(restTemplate.getForObject(uri, BeerDto.class));
    }
}
