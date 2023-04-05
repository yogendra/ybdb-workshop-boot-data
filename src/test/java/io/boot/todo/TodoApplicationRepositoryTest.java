package io.boot.todo;

import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test class to test with an external YugabyteDB instance provisioned and managed through
 * Testcontainers framework.
 */
@Testcontainers
@ActiveProfiles("tcysql")
@DisplayName("Todo application test containers YSQL unit test suite")
public class TodoApplicationRepositoryTest extends AbstractTodoApplicationRepositoryTest {

	@Test
	@DisplayName("should validate the ybdb jdbc test-containers url property")
	@Order(1)
	void dataSourceURLProperty() {
		Pattern pattern = Pattern.compile("jdbc:tc:yugabyte:[0-9a-z-.]+:///yugabyte");
		assertThat(properties.getUrl()).isNotNull();
		assertThat(properties.getUrl()).matches(pattern);
	}

}
