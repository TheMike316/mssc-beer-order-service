package org.brewery.beerorderservice.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.beerorderservice.config.JmsConfig;
import org.brewery.beerorderservice.domain.BeerOrderEvent;
import org.brewery.beerorderservice.domain.BeerOrderStatus;
import org.brewery.beerorderservice.domain.mappers.BeerOrderMapper;
import org.brewery.beerorderservice.repositories.BeerOrderRepository;
import org.brewery.beerorderservice.services.BeerOrderManagerService;
import org.brewery.common.model.event.ValidateBeerOrderRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<BeerOrderStatus, BeerOrderEvent> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository repository;
    private final BeerOrderMapper mapper;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> stateContext) {
        Optional.ofNullable((String) stateContext.getMessageHeaders().get(BeerOrderManagerService.BEER_ORDER_ID_HEADER))
                .map(UUID::fromString)
                .ifPresent(id -> {
                    var order = mapper.beerOrderToDto(repository.getOne(id));
                    var message = new ValidateBeerOrderRequest(order);

                    log.info("Sending allocate order request for order {}", order.getId());

                    jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE, message);
                });
    }
}

