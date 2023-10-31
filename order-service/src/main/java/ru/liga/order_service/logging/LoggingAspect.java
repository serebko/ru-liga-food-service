package ru.liga.order_service.logging;

import advice.EntityException;
import advice.ExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut(value = "execution(public * ru.liga.order_service.service.OrderService.postNewOrder(..))")
    public void processPostNewOrderMethod() {}

    @Before("processPostNewOrderMethod()")
    public void logMethodCall(JoinPoint jp) {
        Object[] args = jp.getArgs();
        String name = jp.getSignature().getName();

        log.info("Вызывается метод: " + name + "\nЕго входные параметры: " + Arrays.toString(args));
    }

    @AfterReturning(value = "processPostNewOrderMethod()", returning = "result")
    public void logMethodResult(JoinPoint jp, Object result) {
        log.info("Возвращенное значение: " + result.toString());
    }
    //TODO: ничего не отбивается, а просто 500ка
    @AfterThrowing(value = "processPostNewOrderMethod()", throwing = "ex")
    public void logThrowingEntityException(JoinPoint jp, EntityException ex) {
        if (ex.getErrorType().equals(ExceptionStatus.RESTAURANT_NOT_FOUND))
            log.warn("Ресторан с таким id не найден");
        else if (ex.getErrorType().equals(ExceptionStatus.CUSTOMER_NOT_FOUND))
            log.warn("Заказчик с таким id не найден");
        else if (ex.getErrorType().equals(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND))
            log.warn("Позиция с таким id не найдена в меню ресторана");
    }

    @AfterThrowing(value = "processPostNewOrderMethod()", throwing = "ex")
    public void logThrowingException(JoinPoint jp, Throwable ex) {
        log.warn("Возникла исключительная ситуация: " + ex.getCause().getMessage());
    }
}
