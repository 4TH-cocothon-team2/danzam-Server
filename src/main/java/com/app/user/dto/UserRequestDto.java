package com.app.user.dto;

import com.app.user.enums.Gender;

public record UserRequestDto (
        Integer age,

        Integer weight,

        Boolean smoke,

        Boolean pregnancy,

        Boolean medication,

        Gender gender
){
}
