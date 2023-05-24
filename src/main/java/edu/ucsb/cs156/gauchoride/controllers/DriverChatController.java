package edu.ucsb.cs156.gauchoride.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.Valid;

import edu.ucsb.cs156.gauchoride.entities.DriverChat;
import edu.ucsb.cs156.gauchoride.services.DriverChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(description= "Driver chat information")
@RequestMapping("/api/driverchats")
@RestController
public class DriverChatController extends ApiController {

    @Autowired
    DriverChatService driverChatService;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of all chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<DriverChat> allChats() {
        return driverChatService.listAllChatMessages();
    }

    @ApiOperation(value = "Get the latest messages based on query parameters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public Iterable<DriverChat> recentChats(
        @ApiParam("limit") @RequestParam int limit
    ) {
        return driverChatService.getRecentChatMessages(limit);
    }


    @ApiOperation(value = "Get a single chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public DriverChat getById(@ApiParam("id") @RequestParam Long id) {
        return driverChatService.getChatById(id);
    }

    @ApiOperation(value="Create a new chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public DriverChat postDriverChat(
        @RequestBody @Valid DriverChat driverChat
    )  throws JsonProcessingException {

        return driverChatService.CreateNewChatMessage(driverChat);
    }

    @ApiOperation(value="Delete a chat message")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteDriverChat(
        @ApiParam("id") @RequestParam Long id
    ) {

        Long deletedMessageId = driverChatService.deleteChatMessageById(id);
        return genericMessage("Message with id %s deleted".formatted(deletedMessageId));
    }

    @ApiOperation(value="Delete all chat messages")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/all")
    public Object deleteAllDriverChat() {
        driverChatService.deleteAllChatMessage();
        return genericMessage("All message have been deleted");
    }
}
