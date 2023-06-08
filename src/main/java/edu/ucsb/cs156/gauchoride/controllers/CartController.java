package edu.ucsb.cs156.gauchoride.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.gauchoride.entities.Cart;
import edu.ucsb.cs156.gauchoride.repositories.CartRepository;

import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;


@Api(description = "Cart")
@RequestMapping("/api/carts")
@RestController

public class CartController extends ApiController {

    @Autowired
    CartRepository cartRepository;

    @ApiOperation(value = "List all carts")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_DRIVER')")
    @GetMapping("/all")
    public Iterable<Cart> allCart() {
        Iterable<Cart> carts = cartRepository.findAll();
        return carts;
    }

    @ApiOperation(value = "Get a single cart")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_DRIVER')")
    @GetMapping("")
    public Cart getById(
            @ApiParam("id") @RequestParam Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Cart.class, id));

        return cart;
    }

    
    @ApiOperation(value = "Create a new cart")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Cart postCart(
        @ApiParam(name = "name", type = "String", value = "name of the cart", example = "Cart1", required = true) 
        @RequestParam String name,
        @ApiParam(name = "capacityPeople", type = "Int", value = "Capacity of the Cart to fit People (e.g. 2 for 2 people)", example = "2", required = true ) 
        @RequestParam int capacityPeople, 
        @ApiParam(name = "capacityWheelchair", type = "Int", value = "Capacity of the Cart to fit Wheelchairs (e.g. 1 for 1 wheelchair)", example = "1", required = true )
        @RequestParam int capacityWheelchair
    ) throws JsonProcessingException {
        
        Cart cart = new Cart();
        cart.setName(name);
        cart.setCapacityPeople(capacityPeople);
        cart.setCapacityWheelchair(capacityWheelchair);

        Cart savedCart = cartRepository.save(cart);
        return savedCart;
    }

    @ApiOperation(value = "Delete a Cart")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteCart(
            @ApiParam(name = "id", type = "Long", value = "ID value of Cart wanting to be deleted (e.g. 4 for cart with ID of 4)", example = "4", required = true ) 
            @RequestParam Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Cart.class, id));

        cartRepository.delete(cart);
        return genericMessage("Cart with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single cart")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Cart updateCart(
            @ApiParam(name = "id", type = "Long", value = "ID value of Cart wanting to be updated (e.g. 4 for cart with ID of 4)", example = "4", required = true )  
            @RequestParam Long id,
            @RequestBody @Valid Cart incoming) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Cart.class, id));

        cart.setName(incoming.getName());
        cart.setCapacityPeople(incoming.getCapacityPeople());
        cart.setCapacityWheelchair(incoming.getCapacityWheelchair());

        cartRepository.save(cart);

        return cart;
    }
}