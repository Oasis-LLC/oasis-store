package com.oasis.onlinestore.contract;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class OrderResponse {
    private String id;
    private User user;
    private Address shippingAddress;
    private List<LineItemResponse> lineItems;
    private StatusResponse status;
}
