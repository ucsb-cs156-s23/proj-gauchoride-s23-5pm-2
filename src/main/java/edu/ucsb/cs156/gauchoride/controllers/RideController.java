package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.entities.Ride;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;
import edu.ucsb.cs156.gauchoride.repositories.RideRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

@Api(description = "Ride")
@RequestMapping("/api/rides")
@RestController
@Slf4j
public class RideController extends ApiController {
    
    @Autowired
    RideRepository rideRepository;

    @Autowired
    UserRepository userRepository;

    @ApiOperation(value = "List all ride requests")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    @GetMapping("/all")
    public Iterable<Ride> allRides() {
        Iterable<Ride> rides = rideRepository.findAll();
        return rides;
    }

    @ApiOperation(value = "List all of user's ride requests")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_RIDER')")
    @GetMapping("/rider")
    public Iterable<Ride> allRiderRides() {
        User currentUser =   getCurrentUser().getUser();
        Iterable<Ride> rides = rideRepository.findAllByRider(currentUser);
        return rides;
    }

    @ApiOperation(value = "Get a ride request by its id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public Ride getById(
        @ApiParam(name="Ride Request's id", type = "Long", value = "id is a number", example = "10",required = true ) 
        @RequestParam Long id) {
            Ride ride = rideRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));
            return ride;
        }

    @ApiOperation(value = "Create a new ride")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public Ride postRides(
        @RequestBody @Valid Ride ride
        )
        throws JsonProcessingException
        {
            Ride savedRide = rideRepository.save(ride);
            return savedRide;
        }

}