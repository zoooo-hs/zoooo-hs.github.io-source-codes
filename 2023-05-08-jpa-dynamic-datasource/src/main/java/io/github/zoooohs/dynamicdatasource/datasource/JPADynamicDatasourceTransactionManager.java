package io.github.zoooohs.dynamicdatasource.datasource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JPADynamicDatasourceTransactionManager implements PlatformTransactionManager {
    public static final String CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER = "current-dynamic-datasource-jpa-transaction-manager";
    private final String basePackage;
    private final Map<String, JpaTransactionManager> jpaTransactionManagerMap = new ConcurrentHashMap<>();
    private final JpaTransactionManager defaultTransactionManager;

    public JPADynamicDatasourceTransactionManager(String basePackage, JpaTransactionManager jpaTransactionManager) {
        this.basePackage = basePackage;
        this.defaultTransactionManager = jpaTransactionManager;
        this.jpaTransactionManagerMap.put("default-datasource", defaultTransactionManager);
    }

    public void set(DatasourceValue datasourceValue) {
        String key = String.valueOf(datasourceValue.hashCode());
        if (!jpaTransactionManagerMap.containsKey(key)){
            jpaTransactionManagerMap.putIfAbsent(key, jpaTransactionManager(datasourceValue));
        }
        JpaTransactionManager found = jpaTransactionManagerMap.get(key);
        TransactionSynchronizationManager.bindResource(CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER, found);
    }

    public void cleanCache() {
        TransactionSynchronizationManager.unbindResourceIfPossible(CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER);
        TransactionSynchronizationManager.bindResource(CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER, defaultTransactionManager);
    }

    public JpaTransactionManager currentTransactionManager() {
        Object cache = TransactionSynchronizationManager.getResource(CURRENT_DYNAMIC_DATASOURCE_JPA_TRANSACTION_MANAGER);
        if (cache != null) return (JpaTransactionManager) cache;
        return defaultTransactionManager;
    }

    private JpaTransactionManager jpaTransactionManager(DatasourceValue datasourceValueWrapper) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create(this.getClass().getClassLoader());
        dataSourceBuilder.driverClassName(datasourceValueWrapper.getDriverClassName())
                .url(datasourceValueWrapper.getUrl())
                .username(datasourceValueWrapper.getUsername())
                .password(datasourceValueWrapper.getPassword());
        DataSource datasource = dataSourceBuilder.build();

        try {
            Connection connection = datasource.getConnection();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(datasource);
        localContainerEntityManagerFactoryBean.setPackagesToScan(basePackage);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        localContainerEntityManagerFactoryBean.afterPropertiesSet();
        EntityManagerFactory entityManagerFactory = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();

        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        return currentTransactionManager().getTransaction(definition);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        currentTransactionManager().commit(status);
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        currentTransactionManager().rollback(status);
    }
}
