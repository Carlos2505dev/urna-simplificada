/*
 * Arquivo: VotingService.java
 * Regras de negócio da eleição.
 */
package br.edu.urna.service;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.CandidateCatalog;
import br.edu.urna.model.ElectionReport;
import br.edu.urna.model.ElectionStatus;
import br.edu.urna.model.Office;
import br.edu.urna.repository.CandidateRepository;
import br.edu.urna.repository.VoteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controla o ciclo da votação e a contagem dos votos.
 * Cada votação recebe um identificador novo, assim o relatório inicial começa zerado.
 */
public class VotingService {

    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private ElectionStatus status = ElectionStatus.NOT_STARTED;
    private String electionIdentifier;

    public VotingService(CandidateRepository candidateRepository, VoteRepository voteRepository) {
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
    }

    public void prepareCandidates() {
        if (candidateRepository.isEmpty()) {
            candidateRepository.saveAll(CandidateCatalog.createDefaultCandidates());
        }
    }

    public void startElection() {
        electionIdentifier = UUID.randomUUID().toString();
        status = ElectionStatus.OPEN;
    }

    public void finishElection() {
        status = ElectionStatus.CLOSED;
    }

    public ElectionStatus getStatus() {
        return status;
    }

    public Optional<Candidate> findCandidate(Office office, String number) {
        return candidateRepository.findByOfficeAndNumber(office, number);
    }

    public void registerVote(Office office, String choice) {
        voteRepository.registerVote(electionIdentifier, office, choice);
    }

    public List<ElectionReport> buildReports() {
        List<ElectionReport> reports = new ArrayList<>();
        for (Office office : Office.values()) {
            reports.add(new ElectionReport(
                    office,
                    candidateRepository.findByOffice(office),
                    voteRepository.countVotes(electionIdentifier, office)));
        }
        return reports;
    }
}
