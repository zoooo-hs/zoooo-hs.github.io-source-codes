package io.github.zoooohs.dynamicdatasource.aspect;

import io.github.zoooohs.dynamicdatasource.datasource.JPADynamicDatasourceTransactionManager;
import io.github.zoooohs.dynamicdatasource.datasource.TransactionManagerConfig;
import io.github.zoooohs.dynamicdatasource.util.JpaRepositoryScanner;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@Aspect
public class JpaRepositoryAspect {
    private final Collection<Class<?>> jpaRepositories;

    public JpaRepositoryAspect() {
        JpaRepositoryScanner scanner = new JpaRepositoryScanner();

        Collection<Class<?>> temp;
        try {
            temp =  scanner.findJpaRepositoryInterfaces(TransactionManagerConfig.ENTITY_BASE_PACKAGE);
        } catch (Exception e) {
            temp = Collections.emptyList();
        }
        this.jpaRepositories = temp;
    }

    @Around("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public Object repository(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!JPADynamicDatasourceTransactionManager.isCurrentTransactionForDynamicDatasource()) {
            return joinPoint.proceed();
        }

        Optional<Class<?>> maybeJpaRepositoryClass = jpaRepositoryClass(joinPoint);
        if (maybeJpaRepositoryClass.isEmpty()) {
            return joinPoint.proceed();
        }
        Class<?> jpaRepositoryClass = maybeJpaRepositoryClass.get();
        Object jpaRepositoryInstance = JPADynamicDatasourceTransactionManager.createJpaRepositoryFromCurrentTransactionManager(jpaRepositoryClass);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = jpaRepositoryClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return method.invoke(jpaRepositoryInstance, joinPoint.getArgs());
    }

    private Optional<Class<?>> jpaRepositoryClass(ProceedingJoinPoint joinPoint) {
        return jpaRepositories.stream().filter(joinPoint.getSignature().getDeclaringType()::isAssignableFrom).findAny();
    }
}
