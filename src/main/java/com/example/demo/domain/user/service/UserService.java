package com.example.demo.domain.user.service;


import com.example.demo.domain.user.dto.UserRequestDto;
import com.example.demo.domain.user.dto.UserResponseDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void createProfile(String uuid, UserRequestDto requestDto) {
        //이미 해당 UUID로 등록된 유저가 있는지 확인
        if (userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("이미 등록된 사용자입니다.");
        }

        User newUser = User.builder()
                .userId(uuid)
                .gender(requestDto.gender())
                .age(requestDto.age())
                .weight(requestDto.weight())
                .smoke(requestDto.smoke())
                .pregnancy(requestDto.pregnancy())
                .medication(requestDto.medication())
                .build();
        userRepository.save(newUser);
    }

    @Transactional
    public void updateProfile(String uuid, UserRequestDto requestDto) {
        User existingUser = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        //Entity의 update 메서드를 호출하여 정보 수정
        existingUser.updateProfile(
                requestDto.gender(),
                requestDto.age(),
                requestDto.weight(),
                requestDto.smoke(),
                requestDto.pregnancy(),
                requestDto.medication()
        );
    }

    @Transactional(readOnly = true) // 데이터 조회만 하므로 readOnly = true 설정
    public UserResponseDto getProfile(String uuid) {
        // UUID로 사용자를 찾음. 없으면 에러 발생
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 찾은 User 엔티티를 Response DTO로 변환하여 반환
        return UserResponseDto.from(user);
    }

}
