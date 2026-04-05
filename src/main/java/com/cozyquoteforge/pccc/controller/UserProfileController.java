package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.ChangePasswordRequest;
import com.cozyquoteforge.pccc.dto.UpdateProfileRequest;
import com.cozyquoteforge.pccc.dto.UserDto;
import com.cozyquoteforge.pccc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(@RequestBody ChangePasswordRequest request) {
        userService.changeMyPassword(request);
        return ResponseEntity.ok().build();
    }
}
