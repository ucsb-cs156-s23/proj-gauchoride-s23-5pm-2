package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.testconfig.TestConfig;
import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.entities.Ride;
import edu.ucsb.cs156.gauchoride.repositories.RideRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Map;
import java.time.LocalTime;

@WebMvcTest(controllers = RideController.class)
@Import(TestConfig.class)
public class RideControllerTests extends ControllerTestCase {
    @MockBean
    RideRepository rideRepository;

    @MockBean
    UserRepository userRepository;
    
    
    // Authorization tests for /api/rides/post
    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/rides/post"))
        .andExpect(status().is(403));
    }
 
    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_get_post() throws Exception {
        mockMvc.perform(post("/api/rides/post")).andExpect(status().is(403));
    }
 
    @WithMockUser(roles = { "RIDER" })
    @Test
    public void logged_in_rider_can_post_a_new_ride_request() throws Exception {
        LocalTime startT = LocalTime.parse("14:00:00");
        LocalTime stopT = LocalTime.parse("15:15:00");
        User currentUser = currentUserService.getCurrentUser().getUser();
        User driver = User.builder().id(10L).driver(true).build();
        Ride ride1 = Ride.builder()
        .day("Tuesday")
        .rider(currentUser)
        .driver(driver)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .phoneNumber(8001230101L)
        .build();
         
        String requestBody = mapper.writeValueAsString(ride1);
        when(rideRepository.save(eq(ride1))).thenReturn(ride1);
 
        // act
        MvcResult response = mockMvc.perform(post("/api/rides/post")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .content(requestBody).with(csrf())).andExpect(status().isOk()).andReturn();
 
        // assert
        verify(rideRepository, times(1)).save(ride1);
        String expectedJson = mapper.writeValueAsString(ride1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "RIDER" })
    @Test
    public void logged_in_rider_cannot_post_for_others() throws Exception {
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalTime startT = LocalTime.parse("14:00:00");
        LocalTime stopT = LocalTime.parse("15:15:00");
        User otherUser = User.builder().id(33L).build();
        User driver = User.builder().id(10L).driver(true).build();
        Ride ride1 = Ride.builder()
        .day("Tuesday")
        .rider(otherUser)
        .driver(driver)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .phoneNumber(8001230101L)
        .build();

        Ride ride2 = Ride.builder()
        .day("Tuesday")
        .rider(currentUser)
        .driver(driver)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .phoneNumber(8001230101L)
        .build();

        String requestBody = mapper.writeValueAsString(ride1);
        when(rideRepository.save(eq(ride2))).thenReturn(ride2);

        // act
        MvcResult response = mockMvc.perform(post("/api/rides/post")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .content(requestBody).with(csrf())).andExpect(status().isOk()).andReturn();

        // assert
        verify(rideRepository, times(1)).save(ride2);
        String expectedJson = mapper.writeValueAsString(ride2);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
 
    @WithMockUser(roles = { "RIDER" })
    @Test
    public void logged_in_rider_cannot_post_a_new_ride_request_with_no_driver() throws Exception {
        User currentUser = currentUserService.getCurrentUser().getUser();
        LocalTime startT = LocalTime.parse("14:00:00");
        LocalTime stopT = LocalTime.parse("15:15:00");
        User driver = User.builder().id(10L).driver(false).build();
        Ride ride = Ride.builder()
        .day("Tuesday")
        .rider(currentUser)
        .driver(driver)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .phoneNumber(8001230101L)
        .build();
 
        String requestBody = mapper.writeValueAsString(ride);
        when(rideRepository.save(eq(ride))).thenReturn(ride);
 
        // Act
        mockMvc.perform(post("/api/rides/post").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(requestBody).with(csrf())).andExpect(status().isBadRequest());

        // Assert
        verify(rideRepository, times(0)).save(ride);
    }
    
    
    // Authorization tests for getById
    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/rides?id=7"))
        .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/rides?id=7")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void test_that_logged_in_admin_can_get_by_id_when_the_id_exists() throws Exception {
        LocalTime startT = LocalTime.of(14,0);
        LocalTime stopT = LocalTime.of(15,15);
        User student = User.builder().id(33L).build();
        User driver = User.builder().id(10L).driver(true).build();
        Ride ride = Ride.builder()
        .day("Tuesday")
        .rider(student)
        .driver(driver)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .phoneNumber(8001230101L)
        .build();
        
        when(rideRepository.findById(eq(7L))).thenReturn(Optional.of(ride));

        // act
        MvcResult response = mockMvc.perform(get("/api/rides?id=7")).andExpect(status().isOk()).andReturn();

        // assert
        verify(rideRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(ride);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
        
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void test_that_logged_in_admin_cannot_get_by_id_when_the_id_does_not_exist() throws Exception {
        // arrange
        when(rideRepository.findById(eq(7L))).thenReturn(Optional.empty());
        
        // act
        MvcResult response = mockMvc.perform(get("/api/rides?id=7")).andExpect(status().isNotFound()).andReturn();
        
        // assert
         verify(rideRepository, times(1)).findById(eq(7L));
         Map<String, Object> json = responseToJson(response);
         assertEquals("EntityNotFoundException", json.get("type"));
         assertEquals("Ride with id 7 not found", json.get("message"));
    }
}