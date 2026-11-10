/*
 * Arquivo: RepositorioCandidatos.java
 * Contrato de acesso aos candidatos.
 */
package br.edu.urna.repository;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import java.util.List;
import java.util.Optional;

/**
 * Operações de persistência dos candidatos.
 */
public interface RepositorioCandidatos {

    List<Candidato> buscarPorCargo(Cargo cargo);

    Optional<Candidato> buscarPorCargoENumero(Cargo cargo, String numero);

    boolean estaVazio();

    void salvarTodos(List<Candidato> candidatos);
}
