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
     * 주어진 메서드 매개변수가 이 리졸버에 의해 처리되어야 하는지 판단합니다.
     *
     * @param parameter 검사 대상 메서드 매개변수
     * @return `true`이면 매개변수가 `@SignUser` 애노테이션을 가지며 `AuthUserRequest`로 할당 가능함, `false`이면 그 외의 경우
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SignUser.class)
                && AuthUserRequest.class.isAssignableFrom(parameter.getParameterType());
    }


    /**
     * 현재 보안 컨텍스트의 인증된 사용자 정보를 컨트롤러 메서드 파라미터로 제공한다.
     *
     * @return `AuthUserRequest` 현재 인증된 사용자의 요청 정보, 인증된 사용자가 없으면 `null`
     */
    @Override
    public AuthUserRequest resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NotNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
            ) {
        final OAuthUserDetails userDetails = (OAuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return isNull(userDetails) ?
                null :
                userDetails.getUser();
    }
}