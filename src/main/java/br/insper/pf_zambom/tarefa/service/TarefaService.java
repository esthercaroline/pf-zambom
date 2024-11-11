package br.insper.pf_zambom.tarefa.service;

import br.insper.pf_zambom.usuario.UsuarioDTO;
import br.insper.pf_zambom.tarefa.model.Tarefa;
import br.insper.pf_zambom.tarefa.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    private RestTemplate restTemplate = new RestTemplate();

    private static final String VALIDATE_URL = "http://184.72.80.215/usuario/validate";

    public Tarefa criarTarefa(Tarefa tarefa, String token) {
        UsuarioDTO usuario = validarTokenERole(token, "ADMIN");

        // Validações adicionais
        if (tarefa.getTitulo() == null || tarefa.getDescricao() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campos obrigatórios não preenchidos");
        }

        return tarefaRepository.save(tarefa);
    }

    public void deletarTarefa(String id, String token) {
        UsuarioDTO usuario = validarTokenERole(token, "ADMIN");

        if (!tarefaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada");
        }

        tarefaRepository.deleteById(id);
    }

    public List<Tarefa> listarTarefas(String token) {
        validarTokenERole(token, "ADMIN", "DEVELOPER");
        return tarefaRepository.findAll();
    }

    public Tarefa obterTarefa(String id, String token) {
        validarTokenERole(token, "ADMIN", "DEVELOPER");

        return tarefaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }

    // Método para validar o token e o papel do usuário usando RestTemplate
    private UsuarioDTO validarTokenERole(String token, String... rolesPermitidos) {
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token não fornecido");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                    VALIDATE_URL,
                    HttpMethod.GET,
                    entity,
                    UsuarioDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                UsuarioDTO usuario = response.getBody();

                for (String role : rolesPermitidos) {
                    assert usuario != null;
                    if (usuario.getPapel().equalsIgnoreCase(role)) {
                        return usuario;
                    }
                }

                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou expirado");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido ou expirado");
        }
    }
}