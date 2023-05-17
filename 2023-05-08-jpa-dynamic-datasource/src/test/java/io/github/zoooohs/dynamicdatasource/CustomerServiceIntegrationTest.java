package io.github.zoooohs.dynamicdatasource;

import io.github.zoooohs.dynamicdatasource.application.dto.CustomerSignUp;
import io.github.zoooohs.dynamicdatasource.application.service.AccountService;
import io.github.zoooohs.dynamicdatasource.application.service.CustomerService;
import io.github.zoooohs.dynamicdatasource.datasource.DatasourceValue;
import io.github.zoooohs.dynamicdatasource.datasource.JPADynamicDatasourceTransactionManager;
import io.github.zoooohs.dynamicdatasource.datasource.TransactionManagerConfig;
import io.github.zoooohs.dynamicdatasource.domain.model.Account;
import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME)
class CustomerServiceIntegrationTest {
	@Autowired
	CustomerService customerService;

	@Autowired
	AccountService accountService;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	JPADynamicDatasourceTransactionManager jpaDynamicDatasourceTransactionManager;

	@DisplayName("applictaion.properties에 설정한 기본 datasource 이외에 새로운 datasource 를 추가하여 회원 가입 및 조회")
	@Test
	void transaction_with_dynamic_datasource() {
		String name = "zoooohs";
		String rnn = "2305085555555";
		CustomerSignUp signUp = new CustomerSignUp(name, rnn);
		Customer savedCustomer = customerService.signUp(signUp);
		Assertions.assertThat(savedCustomer.getId()).isNotNull();
		Customer customerFoundByRnn = customerService.findByRnn(rnn);
		Assertions.assertThat(customerFoundByRnn).isNotNull();

		// Datasource 변경
		DatasourceValue datasourceValue = new DatasourceValue(
				"jdbc:mariadb://127.0.0.1:10078/bank",
				"org.mariadb.jdbc.Driver",
				"root",
				"db-2"
		);
		jpaDynamicDatasourceTransactionManager.set(datasourceValue);

		// datasource 를 바꿨기 때문에 같은 rnn 저장에도 문제가 없어야한다.
		signUp = new CustomerSignUp(name, rnn);
		savedCustomer = customerService.signUp(signUp);
		Assertions.assertThat(savedCustomer.getId()).isNotNull();
		customerFoundByRnn = customerService.findByRnn(rnn);
		Assertions.assertThat(customerFoundByRnn).isNotNull();


		Account account = accountService.create(savedCustomer.getId());
		Assertions.assertThat(account.getId()).isNotNull();
		Assertions.assertThat(account.getAccountNumber()).isNotNull();
		Assertions.assertThat(account.getBalance()).isEqualTo(0);

		entityManager.clear();
//		// delete all
		customerService.deleteAll();
		jpaDynamicDatasourceTransactionManager.cleanCache();
		customerService.deleteAll();
	}

	@Test
	void runtimeExceptionRollbackAllTx() {
		String name = "zoooohs";
		String rnn = "2305085555555";
		CustomerSignUp signUp = new CustomerSignUp(name, rnn);
		Customer savedCustomer = customerService.signUp(signUp);
		Integer customer1Id = savedCustomer.getId();

		String name2 = "hani";
		String rnn2 = "2305084444444";
		signUp = new CustomerSignUp(name2, rnn2);
		savedCustomer = customerService.signUp(signUp);
		Integer customer2Id = savedCustomer.getId();

		Account account1 = accountService.create(customer1Id);
		Account account2 = accountService.create(customer2Id);

		org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> accountService.send(customer1Id, account1.getAccountNumber(), account2.getAccountNumber(), 1000d));

		// BLOCK ACCOUNT
		Double account1ExpectedBalance = 3000d;
		Double account2ExpectedBalance = 1000d;
		accountService.deposit(customer1Id, account1.getAccountNumber(), account1ExpectedBalance);
		accountService.deposit(customer2Id, account2.getAccountNumber(), account2ExpectedBalance);

		Assertions.assertThat(
						accountService.getBalance(customer1Id, account1.getAccountNumber())
				)
				.isEqualTo(account1ExpectedBalance);
		Assertions.assertThat(
						accountService.getBalance(customer2Id, account2.getAccountNumber())
				)
				.isEqualTo(account2ExpectedBalance);


		accountService.blockAccount(customer2Id, account2.getAccountNumber());
		org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> accountService.send(customer1Id, account1.getAccountNumber(), account2.getAccountNumber(), 1000d));

		/**
		 *  account1, account2 에 rollback 되지 않은 값이 들어있어 entitymanager를 한 번 비움
		 *  실제 환경에선 rollback이 일어난 service 종료 후에 다른 서비스를 호출하면서 entityManager 를 재사용하지 않기 때문에 발생하지 않는 일
		 */
		entityManager.clear();

		Assertions.assertThat(
						accountService.getBalance(customer1Id, account1.getAccountNumber())
				)
				.isEqualTo(account1ExpectedBalance);
		Assertions.assertThat(
						accountService.getBalance(customer2Id, account2.getAccountNumber())
				)
				.isEqualTo(account2ExpectedBalance);

		accountService.unblockAccount(customer2Id, account2.getAccountNumber());
		accountService.send(customer1Id, account1.getAccountNumber(), account2.getAccountNumber(), 1000d);

		Assertions.assertThat(
						accountService.getBalance(customer1Id, account1.getAccountNumber())
				)
				.isEqualTo(account1ExpectedBalance - 1000d);
		Assertions.assertThat(
						accountService.getBalance(customer2Id, account2.getAccountNumber())
				)
				.isEqualTo(account2ExpectedBalance + 1000d);

	}
}
