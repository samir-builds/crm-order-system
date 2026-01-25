package com.samir.crm_order_system.service;

import com.samir.crm_order_system.dto.UserDTO;
import com.samir.crm_order_system.enums.RoleName;
import com.samir.crm_order_system.exception.RoleNotFoundException;
import com.samir.crm_order_system.exception.UserNotFoundException;
import com.samir.crm_order_system.model.Role;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.RoleRepository;
import com.samir.crm_order_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Samir");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.findAll();

        assertEquals(1, users.size());
        assertEquals("Samir", users.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Samir");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getById(1L);

        assertNotNull(found);
        assertEquals("Samir", found.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getById(99L));
    }


    @Test
    void testCreateUser_WithDefaultRole() {
        UserDTO dto = new UserDTO();
        dto.setUsername("Samir");
        dto.setEmail("samir@gmail.com");
        dto.setPassword("12345");

        Role defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setName(RoleName.ROLE_USER);

        when(passwordEncoder.encode("12345")).thenReturn("encoded12345");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(defaultRole));

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("Samir");
        saved.setEmail("samir@gmail.com");
        saved.setPassword("encoded12345");
        saved.setRoles(Set.of(defaultRole));

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.create(dto);

        assertNotNull(result.getId());
        assertEquals("Samir", result.getUsername());
        assertEquals("encoded12345", result.getPassword());
        assertTrue(result.getRoles().contains(defaultRole));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("oldName");
        existing.setEmail("old@mail.com");
        existing.setPassword("oldPass");
        existing.setRoles(new HashSet<>());

        UserDTO dto = new UserDTO();
        dto.setUsername("newName");
        dto.setEmail("new@mail.com");
        dto.setPassword("newPass");
        Role defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setName(RoleName.ROLE_USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(defaultRole));

        User updated = new User();
        updated.setId(1L);
        updated.setUsername("newName");
        updated.setEmail("new@mail.com");
        updated.setPassword("encodedNewPass");
        updated.setRoles(Set.of(defaultRole));

        when(userRepository.save(any(User.class))).thenReturn(updated);

        User result = userService.update(1L, dto);
        assertEquals("newName", result.getUsername());
        assertEquals("encodedNewPass", result.getPassword());
        assertTrue(result.getRoles().contains(defaultRole));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser(){
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
