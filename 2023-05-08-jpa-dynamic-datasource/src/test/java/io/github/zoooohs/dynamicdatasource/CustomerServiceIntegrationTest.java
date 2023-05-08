package io.github.zoooohs.dynamicdatasource;

import io.github.zoooohs.dynamicdatasource.application.dto.CustomerSignUp;
import io.github.zoooohs.dynamicdatasource.application.service.CustomerService;
import io.github.zoooohs.dynamicdatasource.datasource.DatasourceValue;
import io.github.zoooohs.dynamicdatasource.datasource.JPADynamicDatasourceTransactionManager;
import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerServiceIntegrationTest {
	@Autowired
	CustomerService customerService;

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


		// delete all
		customerService.deleteAll();
		jpaDynamicDatasourceTransactionManager.cleanCache();
		customerService.deleteAll();
	}
}
