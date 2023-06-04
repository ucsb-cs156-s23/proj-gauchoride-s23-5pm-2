package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.entities.DriverShift;
import edu.ucsb.cs156.gauchoride.repositories.DriverShiftRepository;
import edu.ucsb.cs156.gauchoride.entities.DriverShift.Weekday;
import edu.ucsb.cs156.gauchoride.testconfig.TestConfig;

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

@WebMvcTest(controllers = DriverShiftController.class)
@Import(TestConfig.class)
public class DriverShiftControllerTests extends ControllerTestCase {
    @MockBean
    DriverShiftRepository driverShiftRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/drivershifts/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_user_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/drivershifts/all")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void logged_in_admin_can_get_all_shifts() throws Exception {
        mockMvc.perform(get("/api/drivershifts/all")).andExpect(status().is(200));
    }

    @WithMockUser(roles = { "DRIVER", "USER" })
    @Test
    public void logged_in__driver_can_get_all_shifts() throws Exception {
        mockMvc.perform(get("/api/drivershifts/all")).andExpect(status().is(200));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void logged_in_admin_can_get_correct_shift_list() throws Exception {
        // arrange
        User driver1 = User.builder().id(1L).build();
        User backup1 = User.builder().id(2L).build();
        User driver2 = User.builder().id(3L).build();
        User backup2 = User.builder().id(4L).build();

        LocalTime start1 = LocalTime.parse("00:00:00");
        LocalTime end1 = LocalTime.parse("12:34:56");
        LocalTime start2 = LocalTime.parse("11:11:11");
        LocalTime end2 = LocalTime.parse("22:22:22");

        DriverShift driverShift1 = DriverShift.builder()
                .driver(driver1)
                .backupDriver(backup1)
                .startTime(start1)
                .endTime(end1)
                .weekday(Weekday.Friday)
                .build();

        DriverShift driverShift2 = DriverShift.builder()
                .driver(driver2)
                .backupDriver(backup2)
                .startTime(start2)
                .endTime(end2)
                .weekday(Weekday.Monday)
                .build();

        ArrayList<DriverShift> expectedDriverShift = new ArrayList<>();
        expectedDriverShift.addAll(Arrays.asList(driverShift1, driverShift2));

        when(driverShiftRepository.findAll()).thenReturn(expectedDriverShift);

        // act
        MvcResult response = mockMvc.perform(get("/api/drivershifts/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(driverShiftRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedDriverShift);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "DRIVER", "USER" })
    @Test
    public void logged_in_driver_can_get_correct_shift_list() throws Exception {
        // arrange
        User driver1 = User.builder().id(1L).build();
        User backup1 = User.builder().id(2L).build();
        User driver2 = User.builder().id(3L).build();
        User backup2 = User.builder().id(4L).build();

        LocalTime start1 = LocalTime.parse("00:00:00");
        LocalTime end1 = LocalTime.parse("12:34:56");
        LocalTime start2 = LocalTime.parse("11:11:11");
        LocalTime end2 = LocalTime.parse("22:22:22");

        DriverShift driverShift1 = DriverShift.builder()
                .driver(driver1)
                .backupDriver(backup1)
                .startTime(start1)
                .endTime(end1)
                .weekday(Weekday.Friday)
                .build();

        DriverShift driverShift2 = DriverShift.builder()
                .driver(driver2)
                .backupDriver(backup2)
                .startTime(start2)
                .endTime(end2)
                .weekday(Weekday.Monday)
                .build();

        ArrayList<DriverShift> expectedDriverShift = new ArrayList<>();
        expectedDriverShift.addAll(Arrays.asList(driverShift1, driverShift2));

        when(driverShiftRepository.findAll()).thenReturn(expectedDriverShift);

        // act
        MvcResult response = mockMvc.perform(get("/api/drivershifts/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(driverShiftRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedDriverShift);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}
