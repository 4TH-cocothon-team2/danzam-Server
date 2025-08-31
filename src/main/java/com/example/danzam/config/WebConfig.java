package com.example.danzam.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final GuestAuthInterceptor guestAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(guestAuthInterceptor)
                .addPathPatterns("/api/**"); // /api/ 로 시작하는 모든 경로에 인터셉터 적용
                //.excludePathPatterns("/api/users"); // 단, 최초 사용자 등록 API는 제외 (필요시)
    }
}
