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

    @ApiOperation(value = "List all rides")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Ride> allRidess() {
        Iterable<Ride> rides = rideRepository.findAll();
        return rides;
    }

    @ApiOperation(value = "Get a ride")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Ride getById(
            @ApiParam("id") @RequestParam Long id) {
            Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));
        return ride;
    }

    @ApiOperation(value = "Create a new ride")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Ride postRides(
        @RequestBody @Valid Ride ride
        )
        throws JsonProcessingException
        {
            Ride savedRide = rideRepository.save(ride);
            return savedRide;
        }

    @ApiOperation(value = "Delete a ride")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRide(
            @ApiParam("id") @RequestParam Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));

        rideRepository.delete(ride);
        return genericMessage("Ride with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single rides")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Ride updateRides(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid Ride incoming) {

        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));


        ride.setDay(incoming.getDay());  
        ride.setStudentName(incoming.getStudentName());
        ride.setDriverName(incoming.getDriverName());
        ride.setCourse(incoming.getCourse());
        ride.setTimeStart(incoming.getTimeStart());
        ride.setTimeStop(incoming.getTimeStop());
        ride.setBuilding(incoming.getBuilding());
        ride.setRoom(incoming.getRoom());
        ride.setPickUp(incoming.getPickUp());

        rideRepository.save(ride);

        return ride;
    }
}