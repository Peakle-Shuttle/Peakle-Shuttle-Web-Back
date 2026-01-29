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

public class AuthorizedUserResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SignUser.class)
                && AuthUserRequest.class.isAssignableFrom(parameter.getParameterType());
    }


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
