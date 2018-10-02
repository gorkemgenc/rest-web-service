package com.github.wallet.restwebservice.aspect;

import com.github.wallet.restwebservice.advice.RateLimiterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Pointcut("@annotation(rateLimit)")
    private void annotatedWithRateLimit(RateLimit rateLimit) {}

    @Pointcut("@within(org.springframework.stereotype.Controller)"
            + " || @within(org.springframework.web.bind.annotation.RestController)")
    private void controllerMethods() {}

    @Before("controllerMethods() && annotatedWithRateLimit(rateLimit)")
    public void rateLimitProcess(final JoinPoint joinPoint,
                                 RateLimit rateLimit) throws RateLimiterException {
        log.debug("RateLimitProcess started...");

        HttpServletRequest request = getRequest(joinPoint.getArgs());
        if (request == null) {
            log.error("HttpRequest not found!");
            return;
        }
        String ip = request.getRemoteHost();
        String url = request.getRequestURI();
        String key = String.format("req:lim:%s:%s", url, ip);
        long count = redisTemplate.opsForValue().increment(key, 1);

        log.debug("[Redis] {} = {}", key, count);

        if (count == 1) {
            redisTemplate.expire(key, rateLimit.duration(), rateLimit.unit());
        }
        if (count > rateLimit.limit()) {
            log.warn("Ip : {}, Try count : {}, url : {}, rateLimit : {}", ip, count, url, rateLimit.limit());
            throw new RateLimiterException("Too many requests within short period. Please wait and try again.", -11);
        }
    }

    private HttpServletRequest getRequest(Object[] args) {

        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                return (HttpServletRequest)arg;
            }
        }
        return null;
    }
}