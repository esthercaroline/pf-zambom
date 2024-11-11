package br.insper.pf_zambom.tarefa.repository;
import br.insper.pf_zambom.tarefa.model.Tarefa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TarefaRepository extends MongoRepository<Tarefa, String> {
}
