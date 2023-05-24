package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.Item;
import com.oasis.onlinestore.service.ItemService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService service;

    @GetMapping
    ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = service.findAll();
        return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping(consumes = {"multipart/form-data"})
    ResponseEntity<?> saveItem(@RequestPart Item item,
                               @RequestPart("image") MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            item.setImage(content);
            service.save(item);
        } catch (Exception e) {
            SimpleResponse res = new SimpleResponse(false, "Failed to create item");
            return new ResponseEntity<SimpleResponse>(res, HttpStatus.BAD_REQUEST);
        }
        SimpleResponse res = new SimpleResponse(true, "Successfully create item");
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.CREATED);
    }
}
