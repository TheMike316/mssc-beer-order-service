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

@Service
@RequiredArgsConstructor
public class BeerOrderManagerServiceImpl implements BeerOrderManagerService {

    private final StateMachineFactory<BeerOrderStatus, BeerOrderEvent> stateMachineFactory;
    private final BeerOrderRepository repository;

    @Override
    @Transactional
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatus.NEW);

        var savedOrder = repository.save(beerOrder);

        return null;
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEvent event) {
        var stateMachine = buildStateMachine(beerOrder);

        var message = MessageBuilder.withPayload(event).build();

        stateMachine.sendEvent(message);
    }

    private StateMachine<BeerOrderStatus, BeerOrderEvent> buildStateMachine(BeerOrder beerOrder) {
        var stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access ->
                        access.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null)));

        stateMachine.start();

        return stateMachine;
    }
}
