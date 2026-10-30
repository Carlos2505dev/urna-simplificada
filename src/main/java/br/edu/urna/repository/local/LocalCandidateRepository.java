/*
 * Repositório local de candidatos padrão.
 */
package br.edu.urna.repository.local;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.CandidateCatalog;
import br.edu.urna.model.Office;
import br.edu.urna.repository.CandidateRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Mantém os candidatos do catálogo local em memória.
 */
public class LocalCandidateRepository implements CandidateRepository {

    private final List<Candidate> candidates = new ArrayList<>(CandidateCatalog.createDefaultCandidates());

    @Override
    public List<Candidate> findByOffice(Office office) {
        return candidates.stream()
                .filter(candidate -> candidate.getOffice() == office)
                .toList();
    }

    @Override
    public Optional<Candidate> findByOfficeAndNumber(Office office, String number) {
        return candidates.stream()
                .filter(candidate -> candidate.getOffice() == office)
                .filter(candidate -> candidate.getNumber().equals(number))
                .findFirst();
    }

    @Override
    public boolean isEmpty() {
        return candidates.isEmpty();
    }

    @Override
    public void saveAll(List<Candidate> candidates) {
        this.candidates.clear();
        this.candidates.addAll(candidates);
    }
}