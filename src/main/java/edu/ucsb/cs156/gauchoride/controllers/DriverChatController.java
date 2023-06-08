package edu.ucsb.cs156.gauchoride.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;

import edu.ucsb.cs156.gauchoride.entities.DriverChat;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.repositories.DriverChatRepository;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDateTime;

@Api(description= "Driver chat information")
@RequestMapping("/api/driverchats")
@RestController
public class DriverChatController extends ApiController {

    @Autowired
    DriverChatRepository driverChatRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of all chat message")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    @GetMapping("/all")
    public Iterable<DriverChat> getAllMessages() {
        return driverChatRepository.findAll();
    }

    @ApiOperation(value = "Get the latest N messages based on query parameters")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    @GetMapping("/list")
    public Iterable<DriverChat> getRecentMessage(
        @ApiParam(name = "limit", required = true, example ="100", value="an integer, representing how many messages to query", type="Long") @RequestParam int limit
    ) {
        Pageable pageable = PageRequest.of(0, limit);
        return driverChatRepository.findAllByOrderByTimeStampDesc(pageable);
    }

    @ApiOperation(value = "Get a single chat message")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    @GetMapping("")
    public DriverChat getMessageById(@ApiParam(name = "id", required = true, example="15", value="id of the message", type="Long") @RequestParam Long id) {
        DriverChat message =  driverChatRepository.findById(id)
        .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));

        return message;
    }

    @ApiOperation(value="Create a new chat message")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DRIVER')")
    @PostMapping("/post")
    public DriverChat postNewMessage(
        @RequestBody String messageContent
    )  throws JsonProcessingException {

        User currentUser = getCurrentUser().getUser();
        DriverChat chatMessage = DriverChat.builder()
        .sender(currentUser)
        .messageContent(messageContent)
        .timeStamp(LocalDateTime.now())
        .build();
      
        return  driverChatRepository.save(chatMessage);
    }

    @ApiOperation(value="Update a driver's message")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    @PutMapping("")
    public DriverChat updateMessageByDriver(
        @ApiParam(name = "id", required = true, example="15", value="id of the message", type="Long") @RequestParam Long id,
        @ApiParam(name = "messageContent", required = true, example="can you pick me up?", value="content of new the message", type="String") @RequestParam String messageContent
    ) {
        User currentUser = getCurrentUser().getUser();
        DriverChat message = driverChatRepository.findByIdAndSender(id, currentUser)
                    .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));
        
        message.setMessageContent(messageContent);
        driverChatRepository.save(message);
        return message;
    }

    @ApiOperation(value="Update a driver's message(admin can edit anyone's messages)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("admin")
    public DriverChat updateMessageByAdmin(
        @ApiParam(name = "id", required = true, example="15", value="id of the message", type="Long") @RequestParam Long id,
        @ApiParam(name = "messageContent", required = true, example="can you pick me up?", value="content of the message", type="String") @RequestParam String messageContent
    ) {
        DriverChat message = driverChatRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));
        
        message.setMessageContent(messageContent);
        driverChatRepository.save(message);
        return message;
    }

    @ApiOperation(value="Delete a chat message if it belongs to you")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    @DeleteMapping()
    public Object deleteMessageByDriver(
        @ApiParam(name = "id", required = true, example="15", value="id of the message", type="Long") @RequestParam Long id
    ) {
        User currentUser = getCurrentUser().getUser();
        DriverChat message = driverChatRepository.findByIdAndSender(id, currentUser)
        .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));

        driverChatRepository.delete(message);
        return genericMessage("Message with id %s deleted.".formatted(id));
    }

    @ApiOperation(value="Delete a chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin")
    public Object deleteMessageByAdmin(
        @ApiParam(name = "id", required = true, example="15", value="id of the message", type="Long") @RequestParam Long id
    ) {
        DriverChat message = driverChatRepository.findById(id)
        .orElseThrow(()->new EntityNotFoundException(DriverChat.class, id));

        driverChatRepository.delete(message);
        return genericMessage("Message with id %s deleted.".formatted(id));
    }

    @ApiOperation(value="Delete all chat messages")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/all")
    public Object deleteAllMessages() {
        driverChatRepository.deleteAll();
        return genericMessage("All messages have been deleted.");
    }
}