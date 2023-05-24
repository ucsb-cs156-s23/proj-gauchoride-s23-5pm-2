package edu.ucsb.cs156.gauchoride.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

import javax.validation.Valid;

import edu.ucsb.cs156.gauchoride.entities.DriverChat;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;
import edu.ucsb.cs156.gauchoride.repositories.DriverChatRepository;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(description= "Driver chat information")
@RequestMapping("/api/driverchats")
@RestController
@Slf4j
public class DriverChatController extends ApiController {
    @Autowired
    DriverChatRepository driverChatRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of all chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<DriverChat> allChats() {
        Iterable<DriverChat> chatMessages = driverChatRepository.findAll();
        return chatMessages;
    }

    @ApiOperation(value = "Get a single chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public DriverChat getById(@ApiParam("id") @RequestParam Long id) {
        DriverChat chatMessage = driverChatRepository.findById(id).orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));
        return chatMessage;
    }

    @ApiOperation(value="Create a new chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public DriverChat postDriverChat(
        @RequestBody @Valid DriverChat driverChat
    )  throws JsonProcessingException {

        Long senderId = driverChat.getSender().getId();
        User sender = userRepository.findById(senderId)
        .orElseThrow(()->new EntityNotFoundException(User.class, senderId));

        DriverChat savedDriverChat = driverChatRepository.save(driverChat);

        return savedDriverChat;
    }
}
