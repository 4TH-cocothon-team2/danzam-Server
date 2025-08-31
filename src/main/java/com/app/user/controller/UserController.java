package com.app.user.controller;

import com.app.user.dto.UserRequestDto;
import com.app.user.dto.UserResponseDto;
import com.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1. 사용자 정보 최초 입력 (POST /api/users)
    @PostMapping
    public ResponseEntity<Void> createUserProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserRequestDto requestDto
    ) {
        userService.createProfile(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // '생성됨'을 의미하는 201 상태 코드 반환
    }

    // 2. 사용자 정보 수정 (PATCH /api/users)
    @PatchMapping
    public ResponseEntity<Void> updateUserProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserRequestDto requestDto
    ) {
        userService.updateProfile(userId, requestDto);
        return ResponseEntity.ok().build(); // '성공'을 의미하는 200 상태 코드 반환
    }

    //조회
    @GetMapping
    public ResponseEntity<UserResponseDto> getUserProfile(
            @RequestHeader("X-User-Id") String userId
    ) {
        UserResponseDto userProfile = userService.getProfile(userId);
        return ResponseEntity.ok(userProfile);
    }


}
