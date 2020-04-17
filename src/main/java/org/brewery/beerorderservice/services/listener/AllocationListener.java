package org.brewery.beerorderservice.services.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewery.beerorderservice.config.JmsConfig;
import org.brewery.beerorderservice.services.BeerOrderManagerService;
import org.brewery.common.model.event.AllocateBeerOrderResponse;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationListener {

    private final BeerOrderManagerService service;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void handleAllocationResponse(AllocateBeerOrderResponse response) {
        var dto = response.getBeerOrderDto();
        var pendingInventory = response.getPendingInventory();
        var allocationError = response.getAllocationError();

        log.info("Received allocation response for order {}", dto.getId());

        service.processAllocationResponse(dto, pendingInventory, allocationError);
    }
}
