package com.common.util.tj.aspect;

import com.common.util.ip.HttpRequestUtil;
import com.common.util.tj.annotation.RequestLimit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author
 * @Description: 文件描述
 * @date
 **/
@Component
@Aspect
public class RequestLimitAspect {

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AtomicLong countAto = new AtomicLong(0L);

    @Before("execution(* com.common.util.tj.controller.*.*(..)) && @annotation(limit)")
    public void requestLimit(JoinPoint joinpoint, RequestLimit limit) {
        Object[] args = joinpoint.getArgs();
        Object target = joinpoint.getTarget();
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = HttpRequestUtil.getIpAddr(request);
        String url = request.getRequestURL().toString();
        String key = "req_limit_".concat(url).concat(ip);
        // 加1后看看值
        long curCountV = countAto.getAndIncrement();
        System.out.println(curCountV + " --- " + countAto.incrementAndGet() + " --- " + limit.count());
        if (curCountV > limit.count()) {
            logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
            throw new RuntimeException("超出访问次数限制");
        }
    }

    @After("execution(* com.common.util.tj.controller.*.*(..)) && @annotation(com.common.util.tj.annotation.RequestLimit)")
    public void requestLimitAfter(JoinPoint joinpoint) {
        Object[] args = joinpoint.getArgs();
        Object target = joinpoint.getTarget();
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = HttpRequestUtil.getIpAddr(request);
        String url = request.getRequestURL().toString();
        String key = "req_limit_".concat(url).concat(ip);
        // 加1后看看值
        long curCountV = countAto.getAndIncrement();
        if (curCountV > 5) {
            logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + 5 + "]");
            throw new RuntimeException("超出访问次数限制");
        }
    }
}