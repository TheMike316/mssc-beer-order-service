package org.brewery.beerorderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.beerorderservice.config.JmsConfig;
import org.brewery.common.model.event.ValidateBeerOrderResponse;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationListener {

    private final BeerOrderManagerService managerService;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void handleValidationResponse(ValidateBeerOrderResponse response) {
        log.info("Received validation response for order {}. Order is " + (response.isValid() ? "valid." : "invalid!"),
                response.getBeerOrderId());
        managerService.sendValidationResponseEvent(response.getBeerOrderId(), response.isValid());
    }
}
