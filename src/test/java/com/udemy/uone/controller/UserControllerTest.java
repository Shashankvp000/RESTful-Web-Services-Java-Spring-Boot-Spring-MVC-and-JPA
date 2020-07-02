package com.udemy.uone.controller;

import com.udemy.uone.responsemodel.UserRest;
import com.udemy.uone.service.UserService;
import com.udemy.uone.serviceimpl.UserServiceImpl;
import com.udemy.uone.shareddto.AddressDto;
import com.udemy.uone.shareddto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

	/*
	 * TESTING CLASS
	 */
    @InjectMocks
    UserController userController;


	/*
	 * MOCK CLASSES
	 */
    @Mock
    UserServiceImpl userService;

	/*
	 * ATRIBUTES
	 */
    final String USER_ID = "Ouv7MeOsKAhvEKvnkuwvKzrjxv3pjH";

    UserDto userDto = new UserDto();

	/*
	 * METHODS
	 */
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userDto.setId(1L);
        userDto.setUserId(USER_ID);
        userDto.setFirstName("Shashank");
        userDto.setLastName("V P");
        userDto.setEmail("test@test.com");
        userDto.setEncryptedPassword("sad123sad");
        userDto.setEmailVerificationToken("das123das");
        userDto.setEmailVerificationStatus(true);
        userDto.setAddresses(getAddressesDto());
    }

    @Test
    final void testGetUser() {
        when(userService.getUserbyUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
        assertEquals(USER_ID, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
    }

    private List<AddressDto> getAddressesDto() {
        // Try to add as many fields as real scenario would have
        AddressDto shippingaddressDto = new AddressDto();
        shippingaddressDto.setType("shipping");
        shippingaddressDto.setCity("Bangalore");
        shippingaddressDto.setCountry("India");
        shippingaddressDto.setPostalCode("696969");
        shippingaddressDto.setStreetName("Some Random rd");

        AddressDto billingaddressDto = new AddressDto();
        billingaddressDto.setType("billing");
        billingaddressDto.setCity("Bangalore");
        billingaddressDto.setCountry("India");
        billingaddressDto.setPostalCode("696969");
        billingaddressDto.setStreetName("Some Random rd");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(shippingaddressDto);
        addresses.add(billingaddressDto);

        return addresses;
    }
}