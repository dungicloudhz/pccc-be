package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.RegisterRequest;
import com.cozyquoteforge.pccc.dto.ResetPasswordRequest;
import com.cozyquoteforge.pccc.dto.UpdateUserRequest;
import com.cozyquoteforge.pccc.dto.UserDto;
import com.cozyquoteforge.pccc.entity.Role;
import com.cozyquoteforge.pccc.entity.User;
import com.cozyquoteforge.pccc.service.AuthService;
import com.cozyquoteforge.pccc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody RegisterRequest request, @RequestParam(defaultValue = "ROLE_USER") Role role) {
        User user = authService.createUserByAdmin(request, role);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUserRole(id, request));
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<UserDto> resetUserPassword(@PathVariable UUID id, @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetUserPassword(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}