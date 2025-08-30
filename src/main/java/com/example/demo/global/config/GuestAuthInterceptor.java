package com.example.demo.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GuestAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 요청에서 "Authorization" 헤더를 가져옴
        final String authHeader = request.getHeader("Authorization");

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면 에러 응답 (401 Unauthorized)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false; // 컨트롤러로 요청이 넘어가지 않음
        }

        // 3. "Bearer " 부분을 제외한 순수 UUID 값 추출
        final String uuid = authHeader.substring(7);

        // (선택) UUID 형식 검증 로직 추가 가능

        // 4. 추출한 UUID를 request에 속성으로 저장하여 컨트롤러에서 쉽게 사용하게 함
        request.setAttribute("guestUuid", uuid);

        // 5. 모든 검증을 통과했으므로 true를 반환하여 컨트롤러로 요청을 넘김
        return true;
    }
}