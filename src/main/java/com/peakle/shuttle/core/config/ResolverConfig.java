package com.peakle.shuttle.core.config;

import com.peakle.shuttle.core.resolver.AuthorizedUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/** 커스텀 ArgumentResolver 등록 설정 */
@Configuration
public class ResolverConfig implements WebMvcConfigurer {

    /**
     * Spring MVC에 커스텀 인자 해석기를 등록한다.
     *
     * <p>전달된 resolvers 목록에 AuthorizedUserResolver 인스턴스를 추가하여 컨트롤러 메서드의 인자 해석에 사용되도록 한다.</p>
     *
     * @param resolvers 등록할 HandlerMethodArgumentResolver들을 담고 있는 목록; 이 메서드는 해당 목록에 AuthorizedUserResolver를 추가한다.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthorizedUserResolver());
    }
}