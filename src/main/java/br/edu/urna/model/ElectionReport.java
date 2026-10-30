/*
 * Arquivo: ElectionReport.java
 * Relatório de votação de um cargo.
 */
package br.edu.urna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Guarda a contagem de votos de um cargo, por escolha (número, BRANCO ou NULO).
 */
public class ElectionReport {

    private final Office office;
    private final List<Candidate> candidates;
    private final Map<String, Integer> votesByChoice;

    public ElectionReport(Office office, List<Candidate> candidates, Map<String, Integer> votesByChoice) {
        this.office = office;
        this.candidates = Collections.unmodifiableList(new ArrayList<>(candidates));
        this.votesByChoice = new HashMap<>(votesByChoice);
    }

    public Office getOffice() {
        return office;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public int getVotesFor(String choice) {
        return votesByChoice.getOrDefault(choice, 0);
    }

    public int getTotalVotes() {
        int total = 0;
        for (int votes : votesByChoice.values()) {
            total += votes;
        }
        return total;
    }

    public double getPercentageFor(String choice) {
        int total = getTotalVotes();
        if (total == 0) {
            return 0;
        }
        return getVotesFor(choice) * 100.0 / total;
    }
}
