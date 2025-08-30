package com.example.demo.domain.intake.service;


import com.example.demo.domain.intake.controller.IntakeController;
import com.example.demo.domain.intake.dto.IntakeDto;
import com.example.demo.domain.intake.dto.request.IntakeRequest;
import com.example.demo.domain.intake.entity.IntakeRecord;
import com.example.demo.domain.intake.repository.IntakeRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IntakeService {
    private final IntakeRepository intakeRepository;
    private final UserRepository userRepository;
    @Transactional
    public void intakeRegister(String uuid, IntakeRequest request) {
        // 2. uuid로 사용자를 찾음 (없으면 예외 발생)
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. IntakeRecord 객체를 완성
        IntakeRecord intake = IntakeRecord.builder()
                .userId(user) // 어떤 사용자의 기록인지 연결
                .name(request.name()) // DTO의 필드 이름을 확인하고 맞춰주세요
                .caffeine_mg(request.caffeine_mg())
                .intakeAt(request.intakeAt())
                .build(); // builder를 마무리하는 build() 호출

        intakeRepository.save(intake);
    }

    @Transactional
    public List<IntakeDto> getIntakeList(String uuid) {
        User user = findUserByUuid(uuid);

        // 오늘 날짜의 섭취 기록만 조회
        List<IntakeRecord> intakes = intakeRepository.findByUserAndIntakeAtBetween(
                user,
                LocalDate.now().atStartOfDay(), // 오늘 00:00:00
                LocalDate.now().atTime(LocalTime.MAX) // 오늘 23:59:59
        );

        return intakes.stream()
                .map(IntakeDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public IntakeDto getIntakeDetail(String uuid, String intakeId) {
        User user = findUserByUuid(uuid);
        IntakeRecord intake = intakeRepository.findByIntakeIdAndUser(intakeId, user)
                .orElseThrow(()-> new IllegalArgumentException("해당 id의 섭취 기록이 없습니다"));

        return new IntakeDto(intake);
    }

    @Transactional
    public void modIntake(String uuid, String intakeId, IntakeRequest request) {
        User user = findUserByUuid(uuid);
        IntakeRecord intake = intakeRepository.findByIntakeIdAndUser(intakeId, user)
                .orElseThrow(()-> new IllegalArgumentException("해당 기록이 없습니다"));

        intake.update(request.name(), request.count(), request.capacity(), request.caffeine_mg(), request.intakeAt());
    }

    //삭제
    @Transactional
    public void deleteIntake(String uuid, String intakeId) {
        User user = findUserByUuid(uuid);
        // 섭취 기록 ID와 사용자 정보가 모두 일치하는 기록을 찾습니다.
        IntakeRecord intake = intakeRepository.findByIntakeIdAndUser(intakeId, user)
                .orElseThrow(() -> new IllegalArgumentException("기록을 찾을 수 없거나 삭제할 권한이 없습니다."));
        intakeRepository.deleteById(intakeId);
    }

    //헬퍼 메서드!!!!!!!!!
    private User findUserByUuid(String uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
