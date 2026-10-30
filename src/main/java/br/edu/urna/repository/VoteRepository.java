/*
 * Arquivo: VoteRepository.java
 * Contrato de acesso aos votos.
 */
package br.edu.urna.repository;

import br.edu.urna.model.Office;
import java.util.Map;

/**
 * Operacoes de persistencia dos votos.
 */
public interface VoteRepository {

    void registerVote(String electionIdentifier, Office office, String choice);

    Map<String, Integer> countVotes(String electionIdentifier, Office office);
}
