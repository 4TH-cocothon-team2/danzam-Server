package com.app.intake.controller;


import com.app.intake.dto.IntakeDto;
import com.app.intake.dto.request.IntakeRequest;
import com.app.intake.service.IntakeService;
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
    public ResponseEntity<IntakeDto> getIntakeDetail(@RequestHeader("X-User-Id") String userId, @PathVariable Long intakeId) {
        IntakeDto intakeDetail = intakeService.getIntakeDetail(userId, intakeId);

        return ResponseEntity.ok(intakeDetail);
    }

    //기록 수정
    @PatchMapping("/{intakeId}")
    public ResponseEntity<Void> modIntake(@RequestHeader("X-User-Id") String userId, @RequestBody IntakeRequest request, @PathVariable Long intakeId) {
        intakeService.modIntake(userId, intakeId, request);
        return ResponseEntity.ok().build();
    }

    //삭제
    @DeleteMapping("/{intakeId}")
    public ResponseEntity<Void> deleteIntake(@RequestHeader("X-User-Id") String userId,
                                             @PathVariable Long intakeId) {
        intakeService.deleteIntake(userId, intakeId);
        return ResponseEntity.noContent().build();
    }

}
