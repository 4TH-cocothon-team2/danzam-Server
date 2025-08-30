package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.enums.Gender;

public record UserRequestDto (
        Integer age,

        Integer weight,

        Boolean smoke,

        Boolean pregnancy,

        Boolean medication,

        Gender gender
){
}
