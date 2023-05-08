package io.github.zoooohs.dynamicdatasource.datasource;

import io.github.zoooohs.dynamicdatasource.DynamicDatasourceApplication;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = DynamicDatasourceApplication.class)
public class TransactionManagerConfig {
    public static final String ENTITY_BASE_PACKAGE = "io.github.zoooohs.dynamicdatasource";
    public static final String DYNAMIC_DATASOURCE_TM_NAME = "dynamicDatasourceTransactionManager";

    @Primary
    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages(ENTITY_BASE_PACKAGE)
                .build();
    }

    @Primary
    @Bean("transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory factory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(factory);
        return tm;
    }

    @Bean(DYNAMIC_DATASOURCE_TM_NAME)
    public PlatformTransactionManager dynamicDatasourceTransactionManager(@Qualifier("transactionManager") JpaTransactionManager transactionManager) {
        return new JPADynamicDatasourceTransactionManager(TransactionManagerConfig.ENTITY_BASE_PACKAGE, transactionManager);
    }
}
