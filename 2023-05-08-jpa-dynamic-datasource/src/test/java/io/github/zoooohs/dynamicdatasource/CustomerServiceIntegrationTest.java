package io.github.zoooohs.dynamicdatasource;

import io.github.zoooohs.dynamicdatasource.application.dto.CustomerSignUp;
import io.github.zoooohs.dynamicdatasource.application.service.CustomerService;
import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CustomerServiceIntegrationTest {
	@Autowired
	CustomerService customerService;

	@DisplayName("applictaion.properties에 설정한 기본 datasource에 회원 가입 및 조회")
	@Test
	@Transactional
	void transaction_with_default_datasource_sign_up_and_find_by_rnn() {
		String name = "zoooohs";
		String rnn = "2305085555555";
		CustomerSignUp signUp = new CustomerSignUp(name, rnn);

		Customer savedCustomer = customerService.signUp(signUp);
		Assertions.assertThat(savedCustomer.getId()).isNotNull();

		Customer customerFoundByRnn = customerService.findByRnn(rnn);
		Assertions.assertThat(customerFoundByRnn).isNotNull();
	}
}
