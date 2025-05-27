package com.aop;

import com.response.ApiResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiResponseAspect {
    @Around("execution(* com.controller..*(..))")
    public Object wrapApiResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result == null) {
            return ApiResponse.success("No content");
        }

        if (result instanceof ApiResponse) return result;
        if(result instanceof String) return ApiResponse.successWithMessage(result);
        return ApiResponse.success(result);
    }
}
