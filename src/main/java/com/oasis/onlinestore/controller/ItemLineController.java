package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.domain.OrderLine;
import com.oasis.onlinestore.service.OrderLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lineitems")
public class ItemLineController {
    @Autowired
    private OrderLineService lineItemService;

    @PostMapping
    public OrderLine createLineItem(@RequestBody OrderLine lineItem, @RequestParam long itemId) {
        //  create a new LineItem and save it to the database
        return lineItemService.createLineItem(lineItem);
    }
    @GetMapping("/lineitem/{id}")
    public OrderLine getLineItemById(@PathVariable long id) {
        //  get a LineItem by id
        return lineItemService.getLineItemById(UUID.randomUUID());
    }

    @GetMapping
    public Iterable<OrderLine> getAllLineItems() {
        // retrieve all LineItems from the database
        return lineItemService.getAllLineItems();
    }
    @DeleteMapping("/lineitem/{id}") //  delete a LineItem by id
    public void deleteLineItem(@PathVariable UUID lineItemId) {
        OrderLine lineItem = lineItemService.getLineItemById(lineItemId);
        if (lineItem == null) {
            throw new RuntimeException("LineItem not found - " + lineItemId);
        }
        lineItemService.deleteLineItem(UUID.randomUUID());
    }
    @PutMapping("/lineitem/{id}")
    public OrderLine updateLineItem(@RequestBody OrderLine lineItem, @PathVariable UUID lineItemId) {
        OrderLine existingLineItem = lineItemService.getLineItemById(lineItemId);
        if (existingLineItem == null) {
            throw new RuntimeException("LineItem not found - " + lineItemId);
        }
        return lineItemService.updateLineItem(lineItem);
    }
}

// List of all routes in this controller:
// GET /lineitems
// POST /lineitems
// GET /lineitems/lineitem/{id}
// PUT /lineitems/lineitem/{id}
// DELETE /lineitems/lineitem/{id}
