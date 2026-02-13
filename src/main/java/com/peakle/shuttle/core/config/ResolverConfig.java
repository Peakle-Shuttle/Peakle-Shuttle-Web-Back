package com.peakle.shuttle.core.config;

import com.peakle.shuttle.core.interceptor.VisitorInterceptor;
import com.peakle.shuttle.core.resolver.AuthorizedUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/** 커스텀 ArgumentResolver 및 Interceptor 등록 설정 */
@Configuration
@RequiredArgsConstructor
public class ResolverConfig implements WebMvcConfigurer {

    private final VisitorInterceptor visitorInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthorizedUserResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitorInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/admin/visitor/**");
    }
}