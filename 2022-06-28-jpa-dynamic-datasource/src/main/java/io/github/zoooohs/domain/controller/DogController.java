package io.github.zoooohs.domain.controller;

import io.github.zoooohs.domain.model.Dog;
import io.github.zoooohs.domain.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DogController {

    @Autowired
    DogRepository dogRepository;

    // EntityManagerFactory
    private LocalContainerEntityManagerFactoryBean productEntityManager(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setDataSource(dataSource);
        em.setPackagesToScan("io.github.zoooohs");
        em.setJpaVendorAdapter(vendorAdapter);
        em.afterPropertiesSet();
        return em;
    }

    // DataSource
    private DataSource productDataSource(String driverName, String url, String username, String password) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create(this.getClass().getClassLoader());
        dataSourceBuilder.driverClassName(driverName)
                .url(url)
                .username(username)
                .password(password);
        return dataSourceBuilder.build();
    }


    @GetMapping("/dog")
    public List<Dog> getDogs(
            @Nullable @RequestParam("url") String url,
            @Nullable @RequestParam("driverName") String driverName,
            @Nullable @RequestParam("username") String username,
            @Nullable @RequestParam("password") String password) {
        List<Dog> founds = new ArrayList<>();
        if (driverName == null || url == null || username == null || password == null) {
            founds = dogRepository.findAll();
        } else {
            EntityManagerFactory emf = productEntityManager(productDataSource(driverName, url, username, password))
                    .getNativeEntityManagerFactory();
            EntityManager em = emf.createEntityManager();
            DogRepository dogRepository = new JpaRepositoryFactory(em).getRepository(DogRepository.class);
            founds = dogRepository.findAll();
        }
        return founds;
    }

    @GetMapping("/dog/{dogId}")
    public Dog getDogById(
            @PathVariable("dogId") Long dogId,
            @Nullable @RequestParam("url") String url,
            @Nullable @RequestParam("driverName") String driverName,
            @Nullable @RequestParam("username") String username,
            @Nullable @RequestParam("password") String password) {
        Dog found = null;
        if (driverName == null || url == null || username == null || password == null) {
            found = dogRepository.findById(dogId).orElse(null);
        } else {
            EntityManagerFactory emf = productEntityManager(productDataSource(driverName, url, username, password))
                    .getNativeEntityManagerFactory();
            EntityManager em = emf.createEntityManager();
            DogRepository dogRepository = new JpaRepositoryFactory(em).getRepository(DogRepository.class);
            found = dogRepository.findById(dogId).orElse(null);
        }
        return found;
    }

    @PostMapping("/dog")
    public Dog create(
            @RequestBody Dog dog,
            @Nullable @RequestParam("url") String url,
            @Nullable @RequestParam("driverName") String driverName,
            @Nullable @RequestParam("username") String username,
            @Nullable @RequestParam("password") String password) {
        Dog saved = null;
        if (driverName == null || url == null || username == null || password == null) {
            saved = dogRepository.save(dog);
        } else {
            EntityManagerFactory emf = productEntityManager(productDataSource(driverName, url, username, password))
                    .getNativeEntityManagerFactory();
            EntityManager em = emf.createEntityManager();
            DogRepository dogRepository = new JpaRepositoryFactory(em).getRepository(DogRepository.class);

            try {
                em.getTransaction().begin();
                saved = dogRepository.save(dog);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        return saved;
    }
}
