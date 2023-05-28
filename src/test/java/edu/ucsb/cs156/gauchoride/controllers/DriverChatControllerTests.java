package edu.ucsb.cs156.gauchoride.controllers;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.entities.DriverChat;
import edu.ucsb.cs156.gauchoride.repositories.DriverChatRepository;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.testconfig.TestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = DriverChatController.class)
@Import(TestConfig.class)
public class DriverChatControllerTests extends ControllerTestCase {

    @MockBean
    DriverChatRepository driverChatRepository;

    @MockBean
    UserRepository userRepository;


    // Test if user can access API endpoint when logged out
    @Test
    public void logged_out_user_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/driverchats/all")).andExpect(status().is(403));
    }

    @Test 
    public void logged_out_user_cannot_get_recent_chats() throws Exception {
        mockMvc.perform(get("/api/driverchats/list?limit=5")).andExpect(status().is(403));
    }

    @Test
    public void logged_out_user_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/driverchats?id=2")).andExpect(status().is(403));
    }

    @Test 
    public void logged_out_user_cannot_post() throws Exception {
        mockMvc.perform(post("/api/driverchats/post")).andExpect(status().is(403));
    }

    @Test
    public void logged_out_user_cannot_update() throws Exception {
        mockMvc.perform(put("/api/driverchats?id=2&messageContent=Hello there!!!")).andExpect(status().is(403));
    }

    @Test
    public void logged_out_admin_cannot_update() throws Exception {
        mockMvc.perform(put("/api/driverchats/admin?id=2&messageContent=Hello there!!!")).andExpect(status().is(403));
    }

    @Test
    public void logged_out_user_cannot_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/driverchats?id-5")).andExpect(status().is(403));
    }

    @Test
    public void logged_out_user_cannot_delete_all() throws Exception {
        mockMvc.perform(delete("/api/driverchats/all")).andExpect(status().is(403));
    }

    // Test user roles when user is logged in
    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/driverchats/all")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_list_recent_chats() throws Exception {
        mockMvc.perform(get("/api/driverchats/list?limit=5")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_get_by_id() throws Exception {
        mockMvc.perform(get("/api/driverchats?id=2")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_post() throws Exception {
        mockMvc.perform(post("/api/driverchats/post")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER", "DRIVER" })
    @Test
    public void logged_in_regular_user_and_driver_cannot_access_update() throws Exception {
        mockMvc.perform(put("/api/driverchats/admin?id=2&messageContent=Hello there!!!")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/driverchats?id=5")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_delete_all() throws Exception {
        mockMvc.perform(delete("/api/driverchats/all")).andExpect(status().is(403));
    }

    // Tests functionalities
    @WithMockUser(roles = { "ADMIN", "DRIVER", "USER" })
    @Test
    public void logged_in_admin_or_driver_can_get_all() throws Exception {
        mockMvc.perform(get("/api/driverchats/all")).andExpect(status().is(200));
    }

    @WithMockUser(roles = { "ADMIN", "DRIVER", "USER" })
    @Test
    public void logged_in_admin_or_driver_can_get_all_messages() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        User user1 = User.builder().id(99L).build();
        User user2 = User.builder().id(100L).build();
        LocalDateTime ldt1 = LocalDateTime.parse("2023-05-27T00:00:00");
        LocalDateTime ldt2 = LocalDateTime.parse("2023-05-26T00:00:00");
        LocalDateTime ldt3 = LocalDateTime.parse("2023-05-25T00:00:00");

        DriverChat chat1 = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt1)
                    .build();

        DriverChat chat2 = DriverChat.builder()
        .messageContent("I need to be picked up at 1:15pm.")
        .sender(user1)
        .timeStamp(ldt2)
        .build();

        DriverChat chat3 = DriverChat.builder()
        .messageContent("I cancelled my ride.")
        .sender(user2)
        .timeStamp(ldt3)
        .build();

        ArrayList<DriverChat> expectedMessages = new ArrayList<>();
        expectedMessages.addAll(Arrays.asList(chat1, chat2, chat3));
        when(driverChatRepository.findAll()).thenReturn(expectedMessages);

         // Act
         MvcResult response = mockMvc.perform(get("/api/driverchats/all"))
         .andExpect(status().isOk()).andReturn();

        // Assert

        verify(driverChatRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedMessages);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "DRIVER", "USER" })
    @Test
    public void logged_in_admin_or_driver_can_get_recent_chats() throws Exception {
        mockMvc.perform(get("/api/driverchats/list?limit=5")).andExpect(status().is(200));
    }

    @WithMockUser(roles = { "ADMIN", "DRIVER", "USER" })
    @Test
    public void logged_in_admin_or_driver_can_get_recent_chats_with_order() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        User user1 = User.builder().id(99L).build();
        LocalDateTime ldt1 = LocalDateTime.parse("2023-05-27T00:00:00");
        LocalDateTime ldt2 = LocalDateTime.parse("2023-05-26T00:00:00");

        DriverChat chat1 = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt1)
                    .build();

        DriverChat chat2 = DriverChat.builder()
        .messageContent("I need to be picked up at 1:15pm.")
        .sender(user1)
        .timeStamp(ldt2)
        .build();

        ArrayList<DriverChat> expectedMessages = new ArrayList<>();
        expectedMessages.addAll(Arrays.asList(chat1, chat2));

        Pageable pageable = PageRequest.of(0, 2);
        when(driverChatRepository.findAllByOrderByTimeStampDesc(pageable)).thenReturn(expectedMessages);

        MvcResult response = mockMvc.perform(get("/api/driverchats/list?limit=2"))
         .andExpect(status().isOk()).andReturn();

        verify(driverChatRepository, times(1)).findAllByOrderByTimeStampDesc(pageable);
        String expectedJson = mapper.writeValueAsString(expectedMessages);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "DRIVER", "USER" })
    @Test
    public void logged_in_driver_can_get_by_id_when_the_id_exists() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt)
                    .build();
        
        when(driverChatRepository.findById(eq(1L))).thenReturn(Optional.of(chat));

        // Act
        MvcResult response = mockMvc.perform(get("/api/driverchats?id=1")).andExpect(status().isOk()).andReturn();


        // Assert
        verify(driverChatRepository, times(1)).findById(1L);
        String expectedJson = mapper.writeValueAsString(chat);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "DRIVER", "USER" })
    @Test
    public void logged_in_driver_cannot_get_by_id_when_the_id_does_not_exists() throws Exception {
        // Setup
        when(driverChatRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act
        MvcResult response = mockMvc.perform(get("/api/driverchats?id=1")).andExpect(status().isNotFound()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).findById(1L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("DriverChat with id 1 not found", json.get("message"));
    }

    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void logged_in_admin_can_update_message_owned_by_others() throws Exception {
        User otherUser = User.builder().id(999L).build();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-28T00:00:00");
        DriverChat message = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(otherUser)
                    .timeStamp(ldt)
                    .build();
        
        DriverChat updatedMessage = DriverChat.builder()
        .messageContent("hello there!")
        .sender(otherUser)
        .timeStamp(ldt)
        .build();

        when(driverChatRepository.findById(eq(999L))).thenReturn(Optional.of(message));
         // Act
        MvcResult response = mockMvc.perform(put("/api/driverchats/admin?id=999&messageContent=hello there!").with(csrf()))
                .andExpect(status().isOk()).andReturn();


        verify(driverChatRepository, times(1)).findById(999L);
        verify(driverChatRepository, times(1)).save(updatedMessage);
        String expectedJson = mapper.writeValueAsString(updatedMessage);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void logged_in_admin_cannot_update_message_that_does_not_exist() throws Exception {
        User otherUser = User.builder().id(100L).build();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-28T00:00:00");
        DriverChat message = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(otherUser)
                    .timeStamp(ldt)
                    .build();

        when(driverChatRepository.findById(eq(999L))).thenReturn(Optional.of(message));
         // Act
        MvcResult response = mockMvc.perform(put("/api/driverchats/admin?id=1255&messageContent=hello there!").with(csrf()))
                .andExpect(status().isNotFound()).andReturn();


        verify(driverChatRepository, times(1)).findById(1255L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("DriverChat with id 1255 not found", json.get("message"));
    }

    @WithMockUser(roles = {"DRIVER", "USER"})
    @Test
    public void logged_in_driver_cannot_update_message_owned_by_others() throws Exception {
        User currentUser = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999L).build();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-28T00:00:00");
        DriverChat message = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(otherUser)
                    .timeStamp(ldt)
                    .build();

        when(driverChatRepository.findByIdAndSender(eq(999L), eq(otherUser))).thenReturn(Optional.of(message));
         // Act
        MvcResult response = mockMvc.perform(put("/api/driverchats?id=999&messageContent=hello there!").with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        verify(driverChatRepository, times(1)).findByIdAndSender(999L, currentUser);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("DriverChat with id 999 not found", json.get("message"));
    }

    @WithMockUser(roles = {"DRIVER", "USER"})
    @Test
    public void logged_in_driver_can_update_message_owned_by_themselves() throws Exception {
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-28T00:00:00");
        DriverChat message = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt)
                    .build();

        DriverChat updatedMessage = DriverChat.builder()
        .messageContent("hello there!")
        .sender(currentUser)
        .timeStamp(ldt)
        .build();

        when(driverChatRepository.findByIdAndSender(eq(999L), eq(currentUser))).thenReturn(Optional.of(message));
        // Act
        MvcResult response = mockMvc.perform(put("/api/driverchats?id=999&messageContent=hello there!").with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(driverChatRepository, times(1)).findByIdAndSender(999L, currentUser);
        verify(driverChatRepository, times(1)).save(updatedMessage);
        String expectedJson = mapper.writeValueAsString(updatedMessage);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void logged_in_admin_can_delete_all() throws Exception {
        // Act
        MvcResult response = mockMvc.perform(delete("/api/driverchats/all").with(csrf())).andExpect(status().isOk()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).deleteAll();
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals("All messages have been deleted.", jsonResponse.get("message"));
    }

    @WithMockUser(roles = { "DRIVER", "USER" })
    @Test
    public void logged_in_driver_cannot_delete_message_belongs_to_other_user() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999L).build();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(otherUser)
                    .timeStamp(ldt)
                    .build();
        
        when(driverChatRepository.findByIdAndSender(eq(1L), eq(otherUser))).thenReturn(Optional.of(chat));

        // Act
        MvcResult response = mockMvc.perform(delete("/api/driverchats?id=1").with(csrf())).andExpect(status().isNotFound()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).findByIdAndSender(1L, currentUser);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("DriverChat with id 1 not found", json.get("message"));
    }

    @WithMockUser(roles = { "DRIVER", "USER" })
    @Test
    public void logged_in_driver_can_delete_own_message() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt)
                    .build();
        
        when(driverChatRepository.findByIdAndSender(eq(1L), eq(currentUser))).thenReturn(Optional.of(chat));

        // Act
        MvcResult response = mockMvc.perform(delete("/api/driverchats?id=1").with(csrf())).andExpect(status().isOk()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).findByIdAndSender(1L, currentUser);
        verify(driverChatRepository, times(1)).delete(chat);
        Map<String, Object> json = responseToJson(response);
        assertEquals("Message with id 1 deleted.", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void logged_in_admin_can_delete_own_message() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt)
                    .build();
        
        when(driverChatRepository.findById(eq(1L))).thenReturn(Optional.of(chat));

        // Act
        MvcResult response = mockMvc.perform(delete("/api/driverchats/admin?id=1").with(csrf())).andExpect(status().isOk()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).findById(1L);
        verify(driverChatRepository, times(1)).delete(chat);
        Map<String, Object> json = responseToJson(response);
        assertEquals("Message with id 1 deleted.", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void logged_in_admin_cannot_delete_message_that_does_not_exist() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt)
                    .build();
        
        when(driverChatRepository.findById(eq(1L))).thenReturn(Optional.of(chat));

        // Act
        MvcResult response = mockMvc.perform(delete("/api/driverchats/admin?id=2").with(csrf())).andExpect(status().isNotFound()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).findById(2L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("DriverChat with id 2 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void logged_in_admin_can_delete_message_belongs_to_other_user() throws Exception {
        // Setup
        User otherUser = User.builder().id(999L).build();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(otherUser)
                    .timeStamp(ldt)
                    .build();
        
        when(driverChatRepository.findById(eq(1L))).thenReturn(Optional.of(chat));

        // Act
        MvcResult response = mockMvc.perform(delete("/api/driverchats/admin?id=1").with(csrf())).andExpect(status().isOk()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).findById(1L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("Message with id 1 deleted.", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "DRIVER", "USER" })
    @Test
    public void logged_in_admin_and_driver_can_post_message() throws Exception {
        // Setup
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalDateTime ldt = LocalDateTime.parse("2023-05-27T00:00:00");
        DriverChat chat = DriverChat.builder()
                    .messageContent("Hey Can you pick me up now?")
                    .sender(currentUser)
                    .timeStamp(ldt)
                    .build();

        String requestBody = mapper.writeValueAsString(chat);
        when(driverChatRepository.save(chat)).thenReturn(chat);

        // Act
        MvcResult response = mockMvc.perform(post("/api/driverchats/post")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .content(requestBody).with(csrf())).andExpect(status().isOk()).andReturn();

        // Assert
        verify(driverChatRepository, times(1)).save(chat);
        String expectedJson = mapper.writeValueAsString(chat);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}
