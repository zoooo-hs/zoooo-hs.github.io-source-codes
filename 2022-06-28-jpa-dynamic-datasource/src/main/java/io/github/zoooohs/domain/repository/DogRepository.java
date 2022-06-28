package io.github.zoooohs.domain.repository;

import io.github.zoooohs.domain.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
