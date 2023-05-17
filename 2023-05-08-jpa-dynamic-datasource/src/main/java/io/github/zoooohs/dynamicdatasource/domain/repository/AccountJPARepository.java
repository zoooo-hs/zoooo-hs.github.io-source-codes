package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJPARepository extends JpaRepository<Account, Integer>, AccountRepository {
}
