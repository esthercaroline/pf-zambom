package br.insper.pf_zambom.tarefa.controller;

import br.insper.pf_zambom.tarefa.service.TarefaService;
import br.insper.pf_zambom.tarefa.model.Tarefa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tarefa")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<Tarefa> criarTarefa(@RequestHeader("Authorization") String token, @RequestBody Tarefa tarefa) {
        Tarefa novaTarefa = tarefaService.criarTarefa(tarefa, token);
        return ResponseEntity.ok(novaTarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@RequestHeader("Authorization") String token, @PathVariable String id) {
        tarefaService.deletarTarefa(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> listarTarefas(@RequestHeader("Authorization") String token) {
        List<Tarefa> tarefas = tarefaService.listarTarefas(token);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> obterTarefa(@RequestHeader("Authorization") String token, @PathVariable String id) {
        Tarefa tarefa = tarefaService.obterTarefa(id, token);
        return ResponseEntity.ok(tarefa);
    }
}
