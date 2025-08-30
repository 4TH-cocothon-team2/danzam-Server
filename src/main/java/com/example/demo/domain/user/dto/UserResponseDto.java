package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.enums.Gender;

public record UserResponseDto (
        String userId,
        Gender gender,
        Integer age,
        Integer weight,
        boolean smoke,
        boolean pregnancy,
        boolean medication
){
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getGender(),
                user.getAge(),
                user.getWeight(),
                user.isSmoke(),
                user.isPregnancy(),
                user.isMedication()
        );
    }
}
