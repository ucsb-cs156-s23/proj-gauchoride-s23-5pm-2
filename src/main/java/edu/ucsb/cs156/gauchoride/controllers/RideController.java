package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.entities.Ride;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;
import edu.ucsb.cs156.gauchoride.errors.IllegalRequestException;
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

    @ApiOperation(value = "Create a new ride request")
    @PreAuthorize("hasRole('ROLE_RIDER')")
    @PostMapping("/post")
    public Ride postRides(
        @RequestBody @Valid Ride ride
        )
        throws JsonProcessingException
        {
            User currentUser =   getCurrentUser().getUser();
            User rider = ride.getRider();
            User driver = ride.getDriver();
            if(!rider.equals(currentUser)){
                ride.setRider(currentUser);
            }
            if(!(driver.getDriver())){
                throw new IllegalRequestException();
            }
            Ride savedRide = rideRepository.save(ride);
            return savedRide;
        }

}