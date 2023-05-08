package io.github.zoooohs.dynamicdatasource.aspect;

import io.github.zoooohs.dynamicdatasource.datasource.DynamicalJPADatasource;
import io.github.zoooohs.dynamicdatasource.datasource.JPADynamicDatasourceTransactionManager;
import io.github.zoooohs.dynamicdatasource.datasource.TransactionManagerConfig;
import io.github.zoooohs.dynamicdatasource.domain.repository.AbstractJPARepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

@Component
@Aspect
@RequiredArgsConstructor
public class JPARepositoryImplAspect {
    private final ApplicationContext applicationContext;

    @Around("execution(* io.github.zoooohs.dynamicdatasource.domain.repository.AbstractJPARepository+.*(..))")
    public Object repository(ProceedingJoinPoint joinPoint) throws Throwable {
        Object currentDynamicDatasourceTransactionManager = TransactionSynchronizationManager.getResource(JPADynamicDatasourceTransactionManager.CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER);
        boolean isNotDynamicDatasourceTransaction = currentDynamicDatasourceTransactionManager == null;
        if (isNotDynamicDatasourceTransaction) {
            return joinPoint.proceed();
        }
        Class<?> jpaRepositoryClass = jpaRepositoryClass(joinPoint);
        Object jpaRepositoryInstance = createJpaRepositoryInstance(currentDynamicDatasourceTransactionManager, jpaRepositoryClass);

        Class<?> repositoryImplClass = joinPoint.getTarget().getClass();
        Object repositoryImplInstance = createRepositoryImplInstance(repositoryImplClass, jpaRepositoryClass, jpaRepositoryInstance);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = repositoryImplClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return method.invoke(repositoryImplInstance, joinPoint.getArgs());
    }

    private Object createRepositoryImplInstance(Class<?> repositoryImplClass, Class<?> jpaRepositoryClass, Object jpaRepositoryInstance) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Constructor<?> repositoryImplConstructor = repositoryImplClass.getConstructor(jpaRepositoryClass);
        return repositoryImplConstructor.newInstance(jpaRepositoryInstance);
    }

    private Object createJpaRepositoryInstance(Object currentDynamicDatasourceTransactionManager, Class<?> jpaRepositoryClass) {
        return Optional.of(currentDynamicDatasourceTransactionManager)
                .map(tm -> (JpaTransactionManager) tm)
                .map(JpaTransactionManager::getEntityManagerFactory)
                .map(TransactionSynchronizationManager::getResource)
                .map(found -> (EntityManagerHolder) found)
                .map(EntityManagerHolder::getEntityManager)
                .map(JpaRepositoryFactory::new)
                .map(factory -> factory.getRepository(jpaRepositoryClass))
                .orElseThrow(() -> new RuntimeException("No Dynamic Datasource Transaction Manager"));
    }

    private Class<?> jpaRepositoryClass(ProceedingJoinPoint joinPoint) {
        AnnotatedTypeScanner scanner = new AnnotatedTypeScanner(DynamicalJPADatasource.class);
        scanner.setEnvironment(applicationContext.getEnvironment());
        scanner.setResourceLoader(applicationContext);
        Set<Class<?>> dynamicalDatasourceRepository = scanner.findTypes(TransactionManagerConfig.ENTITY_BASE_PACKAGE);
        AbstractJPARepository<?> dynamicDataSource = (AbstractJPARepository<?>) joinPoint.getTarget();
        return dynamicalDatasourceRepository.stream().filter(dynamicDataSource::support).findAny().orElseThrow(() -> new RuntimeException("NO SUCH REPOSITORY"));
    }
}
