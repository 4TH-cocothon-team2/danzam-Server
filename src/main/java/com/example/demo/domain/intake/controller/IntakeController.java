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
    public ResponseEntity<List<IntakeDto>> getIntakeList(@RequestAttribute("guestUuid") String uuid) {
        List<IntakeDto> intakeList = intakeService.getIntakeList(uuid);
        return ResponseEntity.ok(intakeList);
    }
    //상세 조회
    @GetMapping("/{intakeId}")
    public ResponseEntity<IntakeDto> getIntakeDetail(@RequestAttribute("guestUuid") String uuid, @PathVariable String intakeId) {
        IntakeDto intakeDetail = intakeService.getIntakeDetail(uuid, intakeId);

        return ResponseEntity.ok(intakeDetail);
    }

    //기록 수정
    @PatchMapping("/{intakeId}")
    public ResponseEntity<Void> modIntake(@RequestAttribute("guestUuid") String uuid, @RequestBody IntakeRequest request, @PathVariable String intakeId) {
        intakeService.modIntake(uuid, intakeId, request);
        return ResponseEntity.ok().build();
    }

    //삭제
    @DeleteMapping("/{intakeId}")
    public ResponseEntity<Void> deleteIntake(@RequestAttribute("guestUuid") String uuid,
                                             @PathVariable String intakeId) {
        intakeService.deleteIntake(uuid, intakeId);
        return ResponseEntity.noContent().build();
    }

}
