package com.hairstudio.api.audit;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.users.LoginDTO;
import com.hairstudio.api.dto.users.PasswordUpdateDTO;
import com.hairstudio.api.dto.users.UserCreateDTO;
import com.hairstudio.api.dto.users.UserRegistrationDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @AfterReturning(value = "@annotation(auditable)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Auditable auditable, Object result) {
        String method = joinPoint.getSignature().toShortString();

        String params = Arrays.stream(joinPoint.getArgs())
                .map(this::maskSensitiveArg)
                .collect(Collectors.joining(", "));

        String resultInfo = extractResultInfo(result);

        log.info("✅ [AUDIT] {} executed successfully at {} | Method: {} | Params: [{}] | Result: {}",
                auditable.action(),
                Instant.now(),
                method,
                params,
                resultInfo);
    }

    @AfterThrowing(value = "@annotation(auditable)", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, Auditable auditable, Exception ex) {
        String method = joinPoint.getSignature().toShortString();

        String params = Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));

        log.error("❌ [AUDIT] {} failed at {} | Method: {} | Params: [{}] | Error: {}",
                auditable.action(),
                Instant.now(),
                method,
                params,
                ex.getMessage(), ex);
    }

    private String maskSensitiveArg(Object arg) {
        return switch (arg) {
            case null -> "null";
            case UserRegistrationDTO urd ->
                    String.format("UserRegistrationDTO{email=%s, firstName=%s, lastName=%s, phone=%s}",
                            urd.getEmail(), urd.getFirstName(), urd.getLastName(), urd.getPhoneNumber());
            case UserCreateDTO ucd -> String.format("UserCreateDTO{email=%s, firstName=%s, lastName=%s}",
                    ucd.getEmail(), ucd.getFirstName(), ucd.getLastName());
            case PasswordUpdateDTO ignored -> "PasswordUpdateDTO{masked}";
            case LoginDTO ld -> String.format("LoginDTO{email=%s}", ld.getEmail());
            default -> arg.toString();
        };
    }

    private String extractResultInfo(Object result) {
        switch (result) {
            case null -> {
                return "void";
            }
            case ResultWithoutValue rwov -> {
                return rwov.isSuccess()
                        ? "success"
                        : "failure: " + (rwov.getError() != null ? rwov.getError().description() : "unknown error");
            }
            case ResultWithValue<?> rwv -> {
                if (!rwv.isSuccess()) {
                    return "failure: " + (rwv.getError() != null ? rwv.getError().description() : "unknown error");
                }
                Object value = rwv.getValue();
                if (value == null) return "ResultWithValue<null>";

                try {
                    var clazz = value.getClass();

                    if (clazz.getSimpleName().equals("TokenDTO")) {
                        return "TokenDTO{token=***masked***}";
                    }

                    if (clazz.getSimpleName().equals("PagedUsersDTO")) {
                        var totalCount = clazz.getMethod("getTotalCount").invoke(value);
                        return "PagedUsersDTO{totalCount=" + totalCount + "}";
                    }

                    return value.toString();
                } catch (Exception e) {
                    return "ResultWithValue<unavailable>";
                }
            }
            default -> {
            }
        }

        return result.toString();
    }
}