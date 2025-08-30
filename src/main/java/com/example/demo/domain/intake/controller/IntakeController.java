package com.example.demo.domain.intake.controller;


import com.example.demo.domain.intake.dto.request.IntakeRequest;
import com.example.demo.domain.intake.service.IntakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/intakes")
@RequiredArgsConstructor
public class IntakeController {
    private final IntakeService intakeService;

    @PostMapping
    public ResponseEntity<Void> intakeRegister(
            @RequestAttribute("guestUuid") String uuid,
            @RequestBody IntakeRequest request) {
        intakeService.intakeRegister(uuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
