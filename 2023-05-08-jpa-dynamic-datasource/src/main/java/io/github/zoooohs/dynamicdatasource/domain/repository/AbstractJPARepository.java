package io.github.zoooohs.dynamicdatasource.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Datasource 의 동적 반영을 위해 abstract class 를 따로 분리했다.
 */
public abstract class AbstractJPARepository<REPOSITORY extends JpaRepository> {
    protected final REPOSITORY repository;

    public AbstractJPARepository(REPOSITORY repository) {
        this.repository = repository;
    }

    public boolean support(Class<?> clazz) {
        return clazz.isAssignableFrom(repository.getClass());
    }
}
