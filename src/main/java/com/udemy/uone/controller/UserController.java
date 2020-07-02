package com.udemy.uone.controller;

import com.udemy.uone.exceptions.UserServiceException;
import com.udemy.uone.requestmodel.UserDetailsRequestModel;
import com.udemy.uone.responsemodel.*;
import com.udemy.uone.service.AddressService;
import com.udemy.uone.service.UserService;
import com.udemy.uone.shareddto.AddressDto;
import com.udemy.uone.shareddto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")  //http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    AddressService addressesService;

    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {

        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserbyUserId(id);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDto, UserRest.class);

        return returnValue;
    }


    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        UserRest returnValue = new UserRest();

//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }


    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }


    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }


    // http://localhost:8080/uone/users/userId/addresses
    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json",})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {

        List<AddressesRest> addressesListRestModel = new ArrayList<>();

        List<AddressDto> addressesDto = addressesService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            addressesListRestModel = new ModelMapper().map(addressesDto, listType);

            for (AddressesRest addressRest : addressesListRestModel) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
                addressRest.add(addressLink);

                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressRest.add(userLink);
            }
        }
        return new CollectionModel<>(addressesListRestModel);
    }

    // http://localhost:8080/uone/userId/addresses/addressId
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json",})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId,
                                                    @PathVariable String addressId) {

        AddressDto addressesDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();

//        Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
//        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
//        Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

        AddressesRest addressesRestModel = modelMapper.map(addressesDto, AddressesRest.class);
        addressesRestModel.add(addressLink);
        addressesRestModel.add(userLink);
        addressesRestModel.add(addressesLink);

        return new EntityModel<>(addressesRestModel);

    }
}
