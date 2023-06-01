package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.testconfig.TestConfig;
import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.entities.Ride;
import edu.ucsb.cs156.gauchoride.repositories.RideRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import edu.ucsb.cs156.gauchoride.entities.User;

@WebMvcTest(controllers = RideController.class)
@Import(TestConfig.class)
public class RideControllerTests extends ControllerTestCase {
    @MockBean
    RideRepository rideRepository;

    @MockBean
    UserRepository userRepository;


    // Authorization tests for getById
    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/rides?id=7"))
        .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/rides?id=7")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void test_that_logged_in_admin_can_get_by_id_when_the_id_exists() throws Exception {
        LocalTime startT = LocalTime.of(14,0);
        LocalTime stopT = LocalTime.of(15,15);
        User student = User.builder().id(33L).build();
        User otherUser = User.builder().id(10L).build();
        Ride ride = Ride.builder()
        .day("Tuesday")
        .rider(student)
        .driver(otherUser)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
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
    

    // Authorization tests for /api/rides/post
    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/rides/post"))
        .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_post_a_new_ride() throws Exception {
        LocalTime startT = LocalTime.parse("14:00:00");
        LocalTime stopT = LocalTime.parse("15:15:00");
        User student = User.builder().id(33L).build();
        User otherUser = User.builder().id(10L).build();
        Ride ride1 = Ride.builder()
        .day("Tuesday")
        .rider(student)
        .driver(otherUser)
        .course("CMPSC 156")
        .timeStart(startT)
        .timeStop(stopT)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
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


    // Authorization tests for /api/rides/all
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/rides/all"))
        .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/rides/all")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN", "DRIVER" })
    @Test
    public void logged_in_admin_and_driver_can_get_all() throws Exception {
        mockMvc.perform(get("/api/rides/all")).andExpect(status().is(200));
    }

    
    @WithMockUser(roles = { "ADMIN", "DRIVER" })
    @Test
    public void logged_in_admin_and_driver_can_get_all_rides() throws Exception {
        LocalTime startT1 = LocalTime.of(14,0);
        LocalTime stopT1 = LocalTime.of(15,15);
        User student1 = User.builder().id(33L).build();
        User otherUser1 = User.builder().id(10L).build();
        LocalTime startT2 = LocalTime.of(9,30);
        LocalTime stopT2 = LocalTime.of(10,45);
        User student2 = User.builder().id(45L).build();
        User otherUser2 = User.builder().id(12L).build();
        Ride ride1 = Ride.builder()
        .day("Tuesday")
        .rider(student1)
        .driver(otherUser1)
        .course("CMPSC 156")
        .timeStart(startT1)
        .timeStop(stopT1)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .build();

        Ride ride2 = Ride.builder()
        .day("Monday")
        .rider(student2)
        .driver(otherUser2)
        .course("CMPSC 171")
        .timeStart(startT2)
        .timeStop(stopT2)
        .building("Psychology")
        .room("1924")
        .pickUp("Home")
        .build();
        
        ArrayList<Ride> expectedRides = new ArrayList<>();
        expectedRides.addAll(Arrays.asList(ride1, ride2));

        when(rideRepository.findAll()).thenReturn(expectedRides);

        // act
        MvcResult response = mockMvc.perform(get("/api/rides/all"))
                        .andExpect(status().isOk()).andReturn();

        // assert

        verify(rideRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedRides);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }



    // Authorization tests for /api/rides/rider
    @Test
    public void logged_out_users_cannot_get_ride() throws Exception {
        mockMvc.perform(get("/api/rides/rider"))
        .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_ride() throws Exception {
        mockMvc.perform(get("/api/rides/rider")).andExpect(status().is(200));
    }

    @WithMockUser(roles = { "USER", "RIDER" })
    @Test
    public void logged_in_user_and_rider_can_get_all_their_rides() throws Exception {
        LocalTime startT1 = LocalTime.of(14,0);
        LocalTime stopT1 = LocalTime.of(15,15);
        User student = currentUserService.getCurrentUser().getUser();
        User otherDriver1 = User.builder().id(10L).build();
        LocalTime startT2 = LocalTime.of(9,30);
        LocalTime stopT2 = LocalTime.of(10,45);
        User otherDriver2 = User.builder().id(12L).build();
        Ride ride1 = Ride.builder()
        .day("Tuesday")
        .rider(student)
        .driver(otherDriver1)
        .course("CMPSC 156")
        .timeStart(startT1)
        .timeStop(stopT1)
        .building("South Hall")
        .room("1431")
        .pickUp("Home")
        .build();

        Ride ride2 = Ride.builder()
        .day("Monday")
        .rider(student)
        .driver(otherDriver2)
        .course("CMPSC 171")
        .timeStart(startT2)
        .timeStop(stopT2)
        .building("Psychology")
        .room("1924")
        .pickUp("Home")
        .build();
        
        ArrayList<Ride> expectedRides = new ArrayList<>();
        expectedRides.addAll(Arrays.asList(ride1, ride2));

        when(rideRepository.findAllByRider(student)).thenReturn(expectedRides);

        // act
        MvcResult response = mockMvc.perform(get("/api/rides/rider"))
                        .andExpect(status().isOk()).andReturn();

        // assert

        verify(rideRepository, times(1)).findAllByRider(student);
        String expectedJson = mapper.writeValueAsString(expectedRides);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}