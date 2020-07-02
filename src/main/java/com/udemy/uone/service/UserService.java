package com.udemy.uone.service;

import com.udemy.uone.shareddto.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserbyUserId(String userId);
    UserDto updateUser(String userId, UserDto userDto);
    void deleteUser(String userId);
    List<UserDto> getUsers(int page, int limit);
}
