package com.peakle.shuttle.core.resolver;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.auth.oAuth.OAuthUserDetails;
import com.peakle.shuttle.core.annotation.SignUser;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static java.util.Objects.isNull;

/** @SignUser 어노테이션이 붙은 파라미터에 인증된 사용자 정보를 주입하는 Resolver */
public class AuthorizedUserResolver implements HandlerMethodArgumentResolver {
    /**
     * @SignUser 어노테이션과 AuthUserRequest 타입을 지원하는지 확인합니다.
     *
     * @param parameter 메서드 파라미터
     * @return 지원 여부
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SignUser.class)
                && AuthUserRequest.class.isAssignableFrom(parameter.getParameterType());
    }


    /**
     * SecurityContext에서 인증된 사용자 정보를 추출하여 반환합니다.
     *
     * @param parameter 메서드 파라미터
     * @param mavContainer ModelAndView 컨테이너
     * @param webRequest 웹 요청
     * @param binderFactory 데이터 바인더 팩토리
     * @return 인증된 사용자 정보 (미인증 시 null)
     */
    @Override
    public AuthUserRequest resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NotNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
            ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuthUserDetails userDetails) {
            return userDetails.getUser();
        }

        return null;
    }
}
