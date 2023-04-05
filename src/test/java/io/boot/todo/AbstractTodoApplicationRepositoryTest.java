package io.boot.todo;

import java.util.Optional;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * <p>
 * Base abstract class for testing the {@link ITodoRepository} class. It includes tests
 * for validating injected {@link Autowired} components, fetching all available todo
 * objects, creating a {@link Todo} object, updating a todo object, and deleting a todo
 * object.
 * </p>
 *
 * <p>
 * It uses @DataJpaTest annotation that focuses only on JPA components
 * and @AutoConfigureTestDatabase annotations to not use the embedded database instance.
 * </p>
 *
 * Test cases include various assertions to validate the functionality of the repository,
 * such as checking that all injected entity bean classes are not null and that the
 * repository can create, update, and delete todo objects
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(classes = { AbstractTodoApplicationRepositoryTest.Config.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractTodoApplicationRepositoryTest {

	@Configuration
	@EnableJpaRepositories()
	@EntityScan
	static class Config {

	}

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected EntityManager entityManager;

	@Autowired
	protected ITodoRepository todoRepository;

	@Autowired
	protected DataSourceProperties properties;

	private Todo element;

	@Test
	@DisplayName("should validate all the injected entity bean classes")
	@Order(0)
	void injectedComponentsAreNotNull() {
		assertThat(dataSource).isNotNull();
		assertThat(dataSource instanceof HikariDataSource).isTrue();
		assertThat(entityManager).isNotNull();
		assertThat(todoRepository).isNotNull();
		assertThat(properties).isNotNull();
	}

	@BeforeEach
	void init() {
		element = Todo.builder().task("sample task").build();
	}

	@Test
	@DisplayName("should fetch all the available todo object(s)")
	@Order(100)
	void shouldFetchAllRecords() {
		assertThat(todoRepository.findAll().size()).isEqualTo(5);
	}

	@Test
	@DisplayName("should create a todo object")
	@Order(101)
	void shouldCreateOneRecord() {
		final var todo = todoRepository.save(element);
		final var savedTodo = todoRepository.findById(todo.getId());
		assertThat(savedTodo.isPresent() ? savedTodo.get() : Optional.empty()).isEqualTo(todo);
	}

	@Test
	@DisplayName("should save a todo object")
	@Order(102)
	void shouldUpdateOneRecord() {
		String task = "Using test-containers";
		final var todo = todoRepository.save(element.setTask(task));
		final var savedTodo = todoRepository.findById(todo.getId());
		assertThat(savedTodo.isPresent() ? savedTodo.get().getTask() : Optional.empty()).isEqualTo(task);
	}

	@Test
	@DisplayName("should delete a todo object")
	@Order(103)
	void shouldDeleteOneRecord() {
		final var todo = todoRepository.save(element);
		todoRepository.delete(todo);
		final var savedTodo = todoRepository.findById(todo.getId());
		assertThat(savedTodo).isEmpty();
	}

}
