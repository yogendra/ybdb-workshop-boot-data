package io.boot.todo;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.servlet.function.ServerResponse.created;
import static org.springframework.web.servlet.function.ServerResponse.ok;
import static org.springframework.web.servlet.function.ServerResponse.status;

@SpringBootApplication(proxyBeanMethods = false)
@EnableRetry
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	public RetryTemplate retryTemplate(TodoRetryPolicy todoRetryPolicy) {
		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setMaxInterval(5000);
		retryTemplate.setRetryPolicy(todoRetryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		return retryTemplate;
	}

	@Bean
	RouterFunction<ServerResponse> routeHandler(ITodoService todoService, RetryTemplate retryTemplate) {
		return RouterFunctions.route().path("/v1/todo", builder -> {
			builder.GET("/{id}", req -> {
				Optional<Todo> todo = retryTemplate
						.execute(context -> todoService.findById(UUID.fromString(req.pathVariable("id"))));
				return status(todo.isPresent() ? OK : NOT_FOUND).contentType(APPLICATION_JSON).body(todo);
			}).GET("/page/{limit}", req -> {
				return ok().contentType(APPLICATION_JSON).body(retryTemplate
						.execute(context -> todoService.findByLimit(Integer.parseInt(req.pathVariable("limit")))));
			}).GET(req -> {
				return ok().contentType(APPLICATION_JSON).body(retryTemplate
						.execute(context -> todoService.findAllBySort(Sort.by(Sort.Direction.DESC, "id"))));
			}).POST(req -> {
				Todo todo = req.body(Todo.class);
				return created(new URI("/v1/todo")).contentType(APPLICATION_JSON)
						.body(retryTemplate.execute(context -> todoService.save(todo)));
			}).PUT(req -> {
				Todo todo = req.body(Todo.class);
				return (todo.getId() == null) ? status(BAD_REQUEST).contentType(TEXT_PLAIN).body("Todo id is empty")
						: ok().contentType(APPLICATION_JSON)
								.body(retryTemplate.execute(context -> todoService.save(todo)));
			}).DELETE("/{id}", req -> {
				todoService.deleteById(UUID.fromString(req.pathVariable("id")));
				return ok().build();
			});
		}).build();
	}

}
