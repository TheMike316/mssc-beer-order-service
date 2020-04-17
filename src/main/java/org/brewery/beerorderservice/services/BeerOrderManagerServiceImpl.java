package org.brewery.beerorderservice.services;

import lombok.RequiredArgsConstructor;
import org.brewery.beerorderservice.domain.BeerOrder;
import org.brewery.beerorderservice.domain.BeerOrderEvent;
import org.brewery.beerorderservice.domain.BeerOrderStatus;
import org.brewery.beerorderservice.repositories.BeerOrderRepository;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerServiceImpl implements BeerOrderManagerService {


    private final StateMachineFactory<BeerOrderStatus, BeerOrderEvent> stateMachineFactory;
    private final BeerOrderRepository repository;
    private final BeerOrderStateChangeInterceptor interceptor;

    @Override
    @Transactional
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatus.NEW);

        var savedOrder = repository.save(beerOrder);

        sendBeerOrderEvent(savedOrder, BeerOrderEvent.VALIDATE_ORDER);

        return savedOrder;
    }

    @Override
    @Transactional
    public void sendValidationResponseEvent(UUID beerOrderId, boolean valid) {
        var order = repository.getOne(beerOrderId);

        var event = valid ? BeerOrderEvent.VALIDATION_SUCCESS : BeerOrderEvent.VALIDATION_FAILED;

        sendBeerOrderEvent(order, event);
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEvent event) {
        var stateMachine = buildStateMachine(beerOrder);

        var message = MessageBuilder.withPayload(event)
                .setHeader(BEER_ORDER_ID_HEADER, beerOrder.getId().toString())
                .build();

        stateMachine.sendEvent(message);
    }

    private StateMachine<BeerOrderStatus, BeerOrderEvent> buildStateMachine(BeerOrder beerOrder) {
        var stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access.addStateMachineInterceptor(interceptor);
                    access.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });

        stateMachine.start();

        return stateMachine;
    }
}
