package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.entities.Cart;
import edu.ucsb.cs156.gauchoride.repositories.CartRepository;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
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

@WebMvcTest(controllers = CartController.class)
@Import(TestConfig.class)
public class CartControllerTests extends ControllerTestCase {

        @MockBean
        CartRepository cartRepository;

        @MockBean
        UserRepository userRepository;

    

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/carts/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/carts/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/carts?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

      
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/carts/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/carts/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                Cart cart = Cart.builder()
                                .name("Jeff")
                                .capacityPeople(2)
                                .capacityWheelchair(1)
                                .build();

                when(cartRepository.findById(eq(7L))).thenReturn(Optional.of(cart));

                // act
                MvcResult response = mockMvc.perform(get("/api/carts?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(cartRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(cart);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(cartRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/carts?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(cartRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Cart with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_cart() throws Exception {

                // arrange

                Cart cart1 = Cart.builder()
                                .name("test")
                                .capacityPeople(3)
                                .capacityWheelchair(0)
                                .build();

                Cart cart2 = Cart.builder()
                                .name("test1")
                                .capacityPeople(2)
                                .capacityWheelchair(2)
                                .build();

                ArrayList<Cart> expectedCart = new ArrayList<>();
                expectedCart.addAll(Arrays.asList(cart1, cart2));

                when(cartRepository.findAll()).thenReturn(expectedCart);

                // act
                MvcResult response = mockMvc.perform(get("/api/carts/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(cartRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedCart);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_cart() throws Exception {
                // arrange

                Cart cart1 = Cart.builder()
                                .name("Cart1")
                                .capacityPeople(4)
                                .capacityWheelchair(3)
                                .build();

                when(cartRepository.save(eq(cart1))).thenReturn(cart1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/carts/post?name=Cart1&capacityPeople=4&capacityWheelchair=3")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(cartRepository, times(1)).save(cart1);
                String expectedJson = mapper.writeValueAsString(cart1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_cart() throws Exception {
                // arrange

                Cart cart1 = Cart.builder()
                                .name("Cass")
                                .capacityPeople(3)
                                .capacityWheelchair(4)
                                .build();

                when(cartRepository.findById(eq(15L))).thenReturn(Optional.of(cart1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/carts?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(cartRepository, times(1)).findById(15L);
                verify(cartRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Cart with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_cart_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(cartRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/carts?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(cartRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Cart with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_cart() throws Exception {
                // arrange

                Cart cartOrig = Cart.builder()
                                .name("January")
                                .capacityPeople(2)
                                .capacityWheelchair(5)
                                .build();

                Cart cartEdited = Cart.builder()
                                .name("Krampus")
                                .capacityPeople(1)
                                .capacityWheelchair(6)
                                .build();

                String requestBody = mapper.writeValueAsString(cartEdited);

                when(cartRepository.findById(eq(67L))).thenReturn(Optional.of(cartOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/carts?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(cartRepository, times(1)).findById(67L);
                verify(cartRepository, times(1)).save(cartEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_cart_that_does_not_exist() throws Exception {
                // arrange

                Cart ucsbEditedCart = Cart.builder()
                                .name("absurd")
                                .capacityPeople(20222)
                                .capacityWheelchair(20)
                                .build();

                String requestBody = mapper.writeValueAsString(ucsbEditedCart);

                when(cartRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/carts?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(cartRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Cart with id 67 not found", json.get("message"));

        }
}
