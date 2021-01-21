package com.common.util.count.aspect;

import com.common.util.count.annotation.AnnDemo;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * @Description: 文件描述
 * @date
 **/
@Aspect
@Component
public class AspectDemo {
    @Pointcut(value = "execution(* com.common.util.count.controller..*(..))")
    public void excetionMethod() {
    }

    @Pointcut(value = "execution(* com.common.util.count.controller..*(..)) && @annotation(com.common.util.count.annotation.AnnDemo)")
    public void excetionNote() {
    }

    @Before("excetionMethod()")
    public void testBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            System.out.println(arg);
        }
    }

    @Around(value = "execution(* com.common.util.count.controller..*(..)) && @annotation(com.common.util.count.annotation.AnnDemo)")
    public Object testBeforeNote(ProceedingJoinPoint joinPoint) throws Throwable {
        // 用的最多通知的签名
        Signature signature = joinPoint.getSignature();
        MethodSignature msg = (MethodSignature) signature;
        Object target = joinPoint.getTarget();
        // 获取注解标注的方法
        Method method = target.getClass().getMethod(msg.getName(), msg.getParameterTypes());
        // 通过方法获取注解
        AnnDemo annotation = method.getAnnotation(AnnDemo.class);
        Object proceed;
        // 获取参数
        Object[] args = joinPoint.getArgs();

        System.out.println(annotation.value());
        System.out.println(annotation.isAop());
        for (Object arg : args) {
            System.out.println(arg);
        }

        if (Objects.isNull(annotation) || !annotation.isAop()) {
            System.out.println("无需处理");
            proceed = joinPoint.proceed();
        } else {
            System.out.println("进入aop判断");
            proceed = joinPoint.proceed();
            if (proceed instanceof List) {
                List proceedLst = (List) proceed;
                if (!CollectionUtils.isEmpty(proceedLst)) {

                    ArrayList<String> tbOrderLst = new ArrayList<>();
                    return tbOrderLst;
                }
            }

            System.out.println(proceed);
        }

        return proceed;
    }
}