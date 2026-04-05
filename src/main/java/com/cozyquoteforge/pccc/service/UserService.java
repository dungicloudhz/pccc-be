package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.ChangePasswordRequest;
import com.cozyquoteforge.pccc.dto.ResetPasswordRequest;
import com.cozyquoteforge.pccc.dto.UpdateProfileRequest;
import com.cozyquoteforge.pccc.dto.UpdateUserRequest;
import com.cozyquoteforge.pccc.dto.UserDto;
import com.cozyquoteforge.pccc.entity.Role;
import com.cozyquoteforge.pccc.entity.User;
import com.cozyquoteforge.pccc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    @Transactional
    public UserDto updateUserRole(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent admin from demoting themselves
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        if (currentUsername.equals(user.getUsername()) && request.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Cannot change your own admin role");
        }

        user.setRole(request.getRole());
        userRepository.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent admin from deleting themselves
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        if (currentUsername.equals(user.getUsername())) {
            throw new RuntimeException("Cannot delete your own account");
        }

        userRepository.delete(user);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserDto getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    @Transactional
    public UserDto updateMyProfile(UpdateProfileRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            if (!request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void changeMyPassword(ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update with new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserDto resetUserPassword(UUID id, ResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Reset password to new one
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return convertToDto(user);
    }
}
