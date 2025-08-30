package com.example.demo.domain.intake.controller;


import com.example.demo.domain.intake.dto.IntakeDto;
import com.example.demo.domain.intake.dto.request.IntakeRequest;
import com.example.demo.domain.intake.service.IntakeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intakes")
@RequiredArgsConstructor
public class IntakeController {
    private final IntakeService intakeService;

    //등록
    @PostMapping
    public ResponseEntity<Void> intakeRegister(
            @RequestAttribute("guestUuid") String uuid,
            @RequestBody IntakeRequest request) {
        intakeService.intakeRegister(uuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //목록 조회
    @GetMapping
    public ResponseEntity<List<IntakeDto>> getIntakeList() {
        List<IntakeDto> intakeList = intakeService.getIntakeList();
        return ResponseEntity.ok(intakeList);
    }
    //상세 조회
    @GetMapping("/{intakeId}")
    public ResponseEntity<IntakeDto> getIntakeDetail(@PathVariable String intakeId) {
        IntakeDto intakeDetail = intakeService.getIntakeDetail(intakeId);

        return ResponseEntity.ok(intakeDetail);
    }

}
