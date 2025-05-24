package com.psthiago.desafio_todolist;

import com.psthiago.desafio_todolist.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DesafioTodolistApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testCreateTodoSuccess() {
		var todo = new Todo("Todo 1", "desc todo 1", false, 1);

		webTestClient
				.post()
				.uri("/todos")
				.bodyValue(todo)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(2)
				.jsonPath("$.[0].nome").isEqualTo(todo.getNome())
				.jsonPath("$.[0].descricao").isEqualTo(todo.getDescricao())
				.jsonPath("$.[0].realizado").isEqualTo(todo.getRealizado())
				.jsonPath("$.[0].prioridade").isEqualTo(todo.getPrioridade());
	}


	@Test
	void testCreateTodoFailure() {
		webTestClient
				.post()
				.uri("/todos")
				.bodyValue(
						new Todo("","",false,0)
				).exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void testListTodoSuccess() {
		webTestClient
				.get()
				.uri("/todos")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray();
	}

	@Test
	void testUpdateTodoSuccess() {
		var originalTodo = new Todo("Todo original", "Desc original", false,1);

		var createdTodo = webTestClient
				.post()
				.uri("/todos")
				.bodyValue(originalTodo)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Todo.class)
				.returnResult()
				.getResponseBody()
				.get(0);

		var updatedTodo = new Todo("Tarefa atualizada", "Nova descrição", true, 2);

		webTestClient
				.put()
				.uri("/todos/{id}", createdTodo.getId())
				.bodyValue(updatedTodo)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.[0].nome").isEqualTo(updatedTodo.getNome())
				.jsonPath("$.[0].descricao").isEqualTo(updatedTodo.getDescricao())
				.jsonPath("$.[0].realizado").isEqualTo(updatedTodo.getRealizado())
				.jsonPath("$.[0].prioridade").isEqualTo(updatedTodo.getPrioridade());
	}

	@Test
	void testUpdateTodoFailure() {
		var updatedTodo = new Todo("Tarefa atualizada", "Nova descrição", true, 2);

		webTestClient
				.put()
				.uri("/todos/{id}", 9999)
				.bodyValue(updatedTodo)
				.exchange()
				.expectStatus().isNotFound();

		var invalidTodo = new Todo("", "", false, 0);

		webTestClient
				.put()
				.uri("/todos/{id}", 1)
				.bodyValue(invalidTodo)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void testDeleteTodoSuccess() {
		var novoTodo = new Todo("Todo 1", "desc todo 1", false, 1);

		var createdTodo = webTestClient
				.post()
				.uri("/todos")
				.bodyValue(novoTodo)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Todo.class)
				.returnResult()
				.getResponseBody()
				.get(0);

		webTestClient
				.delete()
				.uri("/todos/{id}", createdTodo.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Todo.class)
				.consumeWith(response -> {
					List<Todo> listaRestante = response.getResponseBody();
					assert listaRestante != null;
					assert listaRestante.stream().noneMatch(todo -> todo.getId().equals(createdTodo.getId()));
				});
	}

	@Test
	void testDeleteTodoFailure() {
		webTestClient
				.delete()
				.uri("/todos/{id}", 9999)
				.exchange()
				.expectStatus().isNotFound();
	}
}
