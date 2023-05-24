package com.oasis.onlinestore.contract;

import com.oasis.onlinestore.domain.AddressType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddressResponse {
    private Integer id;
    private AddressType addressType;
    private String line1;
    private String line2;
    private String city;
    private String street;
    private String zip;
    private String state;
}
