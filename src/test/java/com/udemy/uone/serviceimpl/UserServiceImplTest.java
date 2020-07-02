package com.udemy.uone.serviceimpl;

import com.udemy.uone.exceptions.UserServiceException;
import com.udemy.uone.io.entity.AddressEntity;
import com.udemy.uone.io.entity.UserEntity;
import com.udemy.uone.repository.UserRepository;
import com.udemy.uone.shareddto.AddressDto;
import com.udemy.uone.shareddto.UserDto;
import com.udemy.uone.sharedutils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    /*
	 * TESTING CLASS
	 */

    // InjectMocks annotation is like Autowire but it inject also the mocks
    // defined below which the class need to be tested
    @InjectMocks
    UserServiceImpl userService;

    /*
	 * MOCK CLASSES
	 */

    // Mock object is kind of fake class which we can instantiate and fake the
    // return results their real methods return
    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "sad123sad";
    String encryptedPassword = "das123das";

    UserEntity userEntity;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Shashank");
        userEntity.setLastName("V P");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("sad123sad");
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    final void testGetUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);
        assertEquals("Shashank", userDto.getFirstName());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,

                () -> {
                    userService.getUser("test@test.com");
                }
        );
    }

    @Test
    final void testCreateUser_CreateUserServiceException() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Shashank");
        userDto.setLastName("V P");
        userDto.setPassword("abc123");
        userDto.setEmail("test@test.com");

        assertThrows(UserServiceException.class,

                () -> {
                    userService.createUser(userDto);
                }
        );

    }

    @Test
    final void testCreateUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("sad123sad");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Shashank");
        userDto.setLastName("V P");
        userDto.setPassword("abc123");
        userDto.setEmail("test@test.com");

        UserDto storedUserDetails = userService.createUser(userDto);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("abc123");
        verify(userRepository,times(1)).save(any(UserEntity.class));
    }

    private List<AddressDto> getAddressesDto() {

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

    private List<AddressEntity> getAddressesEntity() {

        List<AddressDto> addresses = getAddressesDto();

        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();

        return new ModelMapper().map(addresses, listType);
    }
}