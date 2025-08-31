package com.app.user.dto;

import com.app.user.entity.User;
import com.app.user.enums.Gender;

public record UserResponseDto (
        String userId,
        Gender gender,
        Integer age,
        Integer weight,
        Boolean smoke,
        Boolean pregnancy,
        Boolean medication
){
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getGender(),
                user.getAge(),
                user.getWeight(),
                user.getSmoke(),
                user.getPregnancy(),
                user.getMedication()
        );
    }
}
