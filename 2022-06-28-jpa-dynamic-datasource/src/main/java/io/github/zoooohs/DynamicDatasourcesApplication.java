package io.github.zoooohs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class DynamicDatasourcesApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicDatasourcesApplication.class, args);
	}

}
