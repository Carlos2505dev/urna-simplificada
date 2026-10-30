/*
 * Arquivo: CandidateRepository.java
 * Contrato de acesso aos candidatos.
 */
package br.edu.urna.repository;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.Office;
import java.util.List;
import java.util.Optional;

/**
 * Operacoes de persistencia dos candidatos.
 */
public interface CandidateRepository {

    List<Candidate> findByOffice(Office office);

    Optional<Candidate> findByOfficeAndNumber(Office office, String number);

    boolean isEmpty();

    void saveAll(List<Candidate> candidates);
}
