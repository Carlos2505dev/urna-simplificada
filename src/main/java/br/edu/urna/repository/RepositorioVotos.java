/*
 * Arquivo: RepositorioVotos.java
 * Contrato de acesso aos votos.
 */
package br.edu.urna.repository;

import br.edu.urna.model.Cargo;
import java.util.Map;

/**
 * Operações de persistência dos votos.
 */
public interface RepositorioVotos {

    void registrarVoto(String identificadorEleicao, Cargo cargo, String escolha);

    Map<String, Integer> contarVotos(String identificadorEleicao, Cargo cargo);
}
