package org.brewery.beerorderservice.config;

import lombok.RequiredArgsConstructor;
import org.brewery.beerorderservice.action.AllocateOrderAction;
import org.brewery.beerorderservice.action.ValidateOrderAction;
import org.brewery.beerorderservice.domain.BeerOrderEvent;
import org.brewery.beerorderservice.domain.BeerOrderStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@RequiredArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatus, BeerOrderEvent> {

    private final ValidateOrderAction validateOrderAction;
    private final AllocateOrderAction allocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatus, BeerOrderEvent> states) throws Exception {
        states.withStates()
                .initial(BeerOrderStatus.NEW)
                .states(EnumSet.allOf(BeerOrderStatus.class))
                .end(BeerOrderStatus.PICKED_UP)
                .end(BeerOrderStatus.DELIVERED)
                .end(BeerOrderStatus.DELIVERY_EXCEPTION)
                .end(BeerOrderStatus.ALLOCATION_EXCEPTION)
                .end(BeerOrderStatus.VALIDATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatus, BeerOrderEvent> transitions) throws Exception {
        //@formatter:off
        transitions
                .withExternal()
                    .source(BeerOrderStatus.NEW)
                    .target(BeerOrderStatus.VALIDATION_PENDING)
                    .event(BeerOrderEvent.VALIDATE_ORDER)
                    .action(validateOrderAction)
                .and()
                    .withExternal()
                        .source(BeerOrderStatus.NEW)
                        .target(BeerOrderStatus.VALIDATED)
                        .event(BeerOrderEvent.VALIDATION_SUCCESS)
                .and()
                    .withExternal()
                        .source(BeerOrderStatus.NEW)
                        .target(BeerOrderStatus.VALIDATION_EXCEPTION)
                        .event(BeerOrderEvent.VALIDATION_FAILED)
                .and()
                    .withExternal()
                        .source(BeerOrderStatus.VALIDATED)
                        .target(BeerOrderStatus.ALLOCATION_PENDING)
                        .event(BeerOrderEvent.ALLOCATE_ORDER)
                        .action(allocateOrderAction);
        //@formatter:on
    }
}
