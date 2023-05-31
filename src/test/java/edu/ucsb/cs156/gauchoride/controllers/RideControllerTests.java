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

        // Authorization tests for /api/rides/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/rides/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/rides/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/rides?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/rides/post

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/rides/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/rides/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // Authorization tests for /api/rides/put

        @Test
        public void logged_out_users_cannot_put() throws Exception {
                mockMvc.perform(put("/api/rides/put"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_put() throws Exception {
                mockMvc.perform(put("/api/rides/put"))
                                .andExpect(status().is(403)); // only admins can put
        }

        // Authorization tests for /api/rides/delete

        @Test
        public void logged_out_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/rides/delete"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/rides/delete"))
                                .andExpect(status().is(403)); // only admins can delete
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
                LocalTime startT = LocalTime.of(14,0);
                LocalTime stopT = LocalTime.of(15,15);
                Ride ride = Ride.builder()
                                .day("Tuesday")
                                .studentName("Bob")
                                .driverName("Tom")
                                .course("CMPSC 156")
                                .timeStart(startT)
                                .timeStop(stopT)
                                .building("South Hall")
                                .room("1431")
                                .pickUp("Home")
                                .build();

                when(rideRepository.findById(eq(7L))).thenReturn(Optional.of(ride));

                // act
                MvcResult response = mockMvc.perform(get("/api/rides?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(ride);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
        
        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(rideRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/rides?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(rideRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Ride with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_rides() throws Exception {
                LocalTime startT1 = LocalTime.of(14,0);
                LocalTime stopT1 = LocalTime.of(15,15);
                LocalTime startT2 = LocalTime.of(9,30);
                LocalTime stopT2 = LocalTime.of(10,45);
                Ride ride1 = Ride.builder()
                                .day("Tuesday")
                                .studentName("Bob")
                                .driverName("Tom")
                                .course("CMPSC 156")
                                .timeStart(startT1)
                                .timeStop(stopT1)
                                .building("South Hall")
                                .room("1431")
                                .pickUp("Home")
                                .build();

                Ride ride2 = Ride.builder()
                                .day("Monday")
                                .studentName("Alex")
                                .driverName("Brad")
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

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ride() throws Exception {
                LocalTime startT = LocalTime.parse("14:00:00");
                LocalTime stopT = LocalTime.parse("15:15:00");
                Ride ride1 = Ride.builder()
                                .day("Tuesday")
                                .studentName("Bob")
                                .driverName("Tom")
                                .course("CMPSC%20156")
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

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_ride() throws Exception {
                User studentUser_id15 = User.builder().id(15L).build();
                User driverUser_id15 = User.builder().id(15L).build();
                LocalTime startT = LocalTime.of(14,0);
                LocalTime stopT = LocalTime.of(15,15);
                Ride ride1 = Ride.builder()
                                .day("Tuesday")
                                .studentName("Bob")
                                .driverName("Tom")
                                .course("CMPSC 156")
                                .timeStart(startT)
                                .timeStop(stopT)
                                .building("South Hall")
                                .room("1431")
                                .pickUp("Home")
                                .build();

                when(rideRepository.findById(eq(15L))).thenReturn(Optional.of(ride1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/rides?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(15L);
                verify(rideRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_ride_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(rideRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/rides?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_ride() throws Exception {
                LocalTime startT = LocalTime.of(14,0);
                LocalTime stopT = LocalTime.of(15,15);
                Ride rideOrig = Ride.builder()
                                .day("Tuesday")
                                .studentName("Bob")
                                .driverName("Tom")
                                .course("CMPSC 156")
                                .timeStart(startT)
                                .timeStop(stopT)
                                .building("South Hall")
                                .room("1431")
                                .pickUp("Home")
                                .build();

                Ride rideEdited = Ride.builder()
                                .day("Tuesday")
                                .studentName("Brad")
                                .driverName("Chad")
                                .course("CMPSC 156")
                                .timeStart(startT)
                                .timeStop(stopT)
                                .building("South Hall")
                                .room("1431")
                                .pickUp("Rec Cen")
                                .build();

                String requestBody = mapper.writeValueAsString(rideEdited);

                when(rideRepository.findById(eq(67L))).thenReturn(Optional.of(rideOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/rides?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(67L);
                verify(rideRepository, times(1)).save(rideEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_ride_that_does_not_exist() throws Exception {
                LocalTime startT = LocalTime.of(14,0);
                LocalTime stopT = LocalTime.of(15,15);
                Ride editedRide = Ride.builder()
                                .day("Tuesday")
                                .studentName("Bob")
                                .driverName("Tom")
                                .course("CMPSC 156")
                                .timeStart(startT)
                                .timeStop(stopT)
                                .building("South Hall")
                                .room("1431")
                                .pickUp("Home")
                                .build();

                String requestBody = mapper.writeValueAsString(editedRide);

                when(rideRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/rides?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 67 not found", json.get("message"));
        }

}