package com.aurionpro.bank.service;

import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.UserDto;
import com.aurionpro.bank.entity.Role;
import com.aurionpro.bank.entity.User;

public interface UserService {
    PageResponse<UserDto> getAllUsers(int page, int size);
    UserDto addUser(UserDto userDto);
    UserDto updateUser(Long userId, UserDto userDto);
    
    UserDto toUserDtoMapper(User user);
    User toUserMapper(UserDto userDto);
    Role getRoleByName(String roleName);
}
