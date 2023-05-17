package io.github.zoooohs.dynamicdatasource.aspect;

import io.github.zoooohs.dynamicdatasource.datasource.JPADynamicDatasourceTransactionManager;
import io.github.zoooohs.dynamicdatasource.datasource.TransactionManagerConfig;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Aspect
@RequiredArgsConstructor
public class JPARepositoryImplAspect {

    @Around("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public Object repository(ProceedingJoinPoint joinPoint) throws Throwable {
        Object currentDynamicDatasourceTransactionManager = TransactionSynchronizationManager.getResource(JPADynamicDatasourceTransactionManager.CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER);
        boolean isNotDynamicDatasourceTransaction = currentDynamicDatasourceTransactionManager == null;
        if (isNotDynamicDatasourceTransaction) {
            return joinPoint.proceed();
        }

        Optional<Class<?>> maybeJpaRepositoryClass = jpaRepositoryClass(joinPoint);
        if (maybeJpaRepositoryClass.isEmpty()) {
            return joinPoint.proceed();
        }
        Class<?> jpaRepositoryClass = maybeJpaRepositoryClass.get();
        Object jpaRepositoryInstance = createJpaRepositoryInstance(currentDynamicDatasourceTransactionManager, jpaRepositoryClass);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = jpaRepositoryClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return method.invoke(jpaRepositoryInstance, joinPoint.getArgs());
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

    private Optional<Class<?>> jpaRepositoryClass(ProceedingJoinPoint joinPoint) throws IOException, ClassNotFoundException {
        List<Class<?>> dynamicalDatasourceRepository = findJpaRepositoryInterfaces(TransactionManagerConfig.ENTITY_BASE_PACKAGE);
        return dynamicalDatasourceRepository.stream().filter(joinPoint.getSignature().getDeclaringType()::isAssignableFrom).findAny();
    }

    private List<Class<?>> findJpaRepositoryInterfaces(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> jpaRepositoryInterfaces = new ArrayList<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(StringUtils.hasText(basePackage) ?
                        basePackage : ClassUtils.getPackageName(getClass())) + "/**/*.class";
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        AssignableTypeFilter assignableTypeFilter = new AssignableTypeFilter(JpaRepository.class);

        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (assignableTypeFilter.match(metadataReader, metadataReaderFactory)) {
                    jpaRepositoryInterfaces.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }

        return jpaRepositoryInterfaces;
    }
}
