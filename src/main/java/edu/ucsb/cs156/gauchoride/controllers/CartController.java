package edu.ucsb.cs156.gauchoride.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ucsb.cs156.gauchoride.entities.Cart;
import edu.ucsb.cs156.gauchoride.repositories.CartRepository;

import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @ApiParam("name") @RequestParam String name,
        @ApiParam("capacityPeople") @RequestParam int capacityPeople, 
        @ApiParam("capacityWheelchair")@RequestParam int capacityWheelchair
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
            @ApiParam("id") @RequestParam Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Cart.class, id));

        cartRepository.delete(cart);
        return genericMessage("Cart with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single cart")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Cart updateCart(
            @ApiParam("id") @RequestParam Long id,
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