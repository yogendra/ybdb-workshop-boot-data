package io.boot.todo;

import java.util.Scanner;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test class to test with an external self/hosted YugabyteDB instance.
 */
@ActiveProfiles("tysql")
@DisplayName("Todo application external YSQL unit test suite")
public class TodoApplicationRepositoryYSQLTest extends AbstractTodoApplicationRepositoryTest {

	@Test
	@DisplayName("should validate the ybdb driver-class name")
	@Order(1)
	void dataSourceDriverProperty() {
		assertThat(properties.getDriverClassName()).isNotNull();
		assertThat(properties.getDriverClassName()).isEqualTo("com.yugabyte.Driver");
	}

	@Test
	@DisplayName("data-source: should validate the ybdb load-balance property")
	@Order(2)
	void dataSourceLoadBalanceProperty() {
		if (dataSource instanceof HikariDataSource hs) {
			if (hs.getDataSourceProperties().containsKey("load-balance")) {
				assertThat(hs.getDataSourceProperties().getProperty("load-balance")).matches("(true|false)");
			}
		}
	}

	@Test
	@DisplayName("connection-string: should validate the ybdb load-balance property")
	@Order(2)
	void connectionStringLoadBalanceProperty() {
		var url = properties.getUrl();
		assertThat(url).isNotNull();
		if (url.contains("load-balance")) {
			var scan = new Scanner(url.substring(url.indexOf("?") + 1));
			assertThat(scan).isNotNull();
			scan.useDelimiter(",");
			while (scan.hasNext()) {
				var opt = scan.next();
				if (opt.contains("load-balance")) {
					assertThat(opt).matches("load-balance=(true|false)");
				}
			}
		}

	}

	@Test
	@DisplayName("data-source: should validate the ybdb certs property if SSL is enabled")
	@Order(3)
	void dataSourceSSLProperty() {
		if (dataSource instanceof HikariDataSource hs) {
			if (hs.getDataSourceProperties().containsKey("sslMode")) {
				assertThat(hs.getDataSourceProperties().getProperty("sslMode"))
						.matches("(allow|prefer|require|verify-full|verify-ca)");
			}
		}
	}

	@Test
	@DisplayName("connection-string: should validate the ybdb certs property if SSL is enabled")
	@Order(3)
	void connectionStringSSLProperty() {
		var url = properties.getUrl();
		assertThat(url).isNotNull();
		if (url.contains("sslMode")) {
			var scan = new Scanner(url.substring(url.indexOf("?") + 1));
			assertThat(scan).isNotNull();
			scan.useDelimiter(",");
			while (scan.hasNext()) {
				var opt = scan.next();
				if (opt.contains("sslMode")) {
					assertThat(opt).matches("sslMode=(allow|prefer|require|verify-full|verify-ca)");
				}
			}
		}
	}

}
