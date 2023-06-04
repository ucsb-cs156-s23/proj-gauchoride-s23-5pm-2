package edu.ucsb.cs156.gauchoride.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.entities.DriverShift;
import edu.ucsb.cs156.gauchoride.repositories.DriverShiftRepository;
import edu.ucsb.cs156.gauchoride.entities.DriverShift.Weekday;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Api(description = "Driver shift information")
@RequestMapping("/api/drivershifts")
@RestController
public class DriverShiftController extends ApiController {
    @Autowired
    DriverShiftRepository driverShiftRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of all driver shifts")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_DRIVER')")
    @GetMapping("/all")
    public Iterable<DriverShift> getAllDriverShifts() {
        Iterable<DriverShift> driverShifts = driverShiftRepository.findAll();
        return driverShifts;
    }
}
