package com.yrgo.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/*
    AspectJ pointcut syntax
    AOP namespace
 */

@Component("performanceTimingAdvice")
public class PerformanceTimingAdvice {
    public Object performTimingMeasurement(ProceedingJoinPoint method) throws Throwable {
        long startTime = System.nanoTime();

        try {
            Object value = method.proceed();
            return value;
        } finally {
            long endTime = System.nanoTime();
            long timeTaken = endTime - startTime;
            System.out.println("Time taken for the method " + method.getSignature().getName() + " from the class "
                    + method.getClass().getCanonicalName() + " took " + timeTaken/1000000 + "ms");
        }

    }
}
