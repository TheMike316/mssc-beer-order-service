package org.brewery.beerorderservice.services;

import lombok.RequiredArgsConstructor;
import org.brewery.beerorderservice.domain.BeerOrder;
import org.brewery.beerorderservice.domain.BeerOrderEvent;
import org.brewery.beerorderservice.domain.BeerOrderStatus;
import org.brewery.beerorderservice.repositories.BeerOrderRepository;
import org.brewery.common.model.BeerOrderDto;
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
    public void processValidationResponse(UUID beerOrderId, boolean valid) {
        var order = repository.getOne(beerOrderId);

        if (valid) {
            sendBeerOrderEvent(order, BeerOrderEvent.VALIDATION_SUCCESS);
            // also send allocate order event; validated order needs to be re-fetched,
            // because the interceptor persists the state
            var validatedOrder = repository.getOne(beerOrderId);
            sendBeerOrderEvent(validatedOrder, BeerOrderEvent.ALLOCATE_ORDER);
        } else {
            sendBeerOrderEvent(order, BeerOrderEvent.VALIDATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void processAllocationResponse(BeerOrderDto dto, boolean pendingInventory, boolean allocationError) {
        BeerOrder order = repository.getOne(dto.getId());

        if (allocationError) {
            sendBeerOrderEvent(order, BeerOrderEvent.ALLOCATION_FAILED);
            return;
        }

        //persist order details
        order.getBeerOrderLines().forEach(line ->
                dto.getBeerOrderLines().stream()
                        .filter(l -> line.getId().equals(l.getId()))
                        .findFirst()
                        .ifPresent(ld -> line.setQuantityAllocated(ld.getAllocatedQuantity())));

        repository.save(order);

        sendBeerOrderEvent(order, pendingInventory ? BeerOrderEvent.ALLOCATION_NO_INVENTORY :
                BeerOrderEvent.ALLOCATION_SUCCESS);
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
