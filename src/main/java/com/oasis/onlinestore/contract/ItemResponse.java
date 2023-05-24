package com.oasis.onlinestore.contract;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class ItemResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private String barcode;
    private int quantity;
    private List<ReviewResponse> reviews = new ArrayList<>();
    private List<ItemResponse> items = new ArrayList<>(); // composite
}
