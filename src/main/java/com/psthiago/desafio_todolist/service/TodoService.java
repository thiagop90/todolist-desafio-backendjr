package com.psthiago.desafio_todolist.service;

import com.psthiago.desafio_todolist.entity.Todo;
import com.psthiago.desafio_todolist.repository.TodoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TodoService {
    private TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> create(Todo todo) {
        todoRepository.save(todo);
        return list();
    }

    public List<Todo> list() {
        Sort sort = Sort.by("prioridade").descending().and(
                Sort.by("nome").ascending()
        );
        return todoRepository.findAll(sort);
    }

    public List<Todo> update(Long id, Todo todo) {
        Todo existente = todoRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Todo com ID: " + id + "não encontrado!"));

        existente.setNome(todo.getNome());
        existente.setDescricao(todo.getDescricao());
        existente.setRealizado(todo.getRealizado());
        existente.setPrioridade(todo.getPrioridade());

        todoRepository.save(existente);
        return list();
    }

    public List<Todo> delete(Long id) {
        todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todo com ID: " + id + "não encontrado!"));

        todoRepository.deleteById(id);
        return list();
    }
}
