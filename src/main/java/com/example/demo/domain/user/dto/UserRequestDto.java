package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.enums.Gender;

public record UserRequestDto (
        Integer age,

        Integer weight,

        boolean smoke,

        boolean pregnancy,

        boolean medication,

        Gender gender
){
}
