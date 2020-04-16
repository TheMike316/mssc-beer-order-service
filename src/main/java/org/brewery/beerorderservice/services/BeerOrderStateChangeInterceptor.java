package org.brewery.beerorderservice.services;

import lombok.RequiredArgsConstructor;
import org.brewery.beerorderservice.domain.BeerOrderEvent;
import org.brewery.beerorderservice.domain.BeerOrderStatus;
import org.brewery.beerorderservice.repositories.BeerOrderRepository;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEvent> {

    private final BeerOrderRepository repository;

    @Override
    public void preStateChange(State<BeerOrderStatus, BeerOrderEvent> state, Message<BeerOrderEvent> message, Transition<BeerOrderStatus, BeerOrderEvent> transition, StateMachine<BeerOrderStatus, BeerOrderEvent> stateMachine) {
        if (message != null) {
            Optional.ofNullable((String) message.getHeaders().get(BeerOrderManagerService.BEER_ORDER_ID_HEADER))
                    .map(UUID::fromString)
                    .ifPresent(beerOrderId -> {
                        var beerOrder = repository.getOne(beerOrderId);
                        beerOrder.setOrderStatus(state.getId());
                        repository.saveAndFlush(beerOrder);
                    });
        }
    }
}
