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
public class RelatorioEleitoral {

    private final Cargo cargo;
    private final List<Candidato> candidatos;
    private final Map<String, Integer> votosPorEscolha;

    public RelatorioEleitoral(Cargo cargo, List<Candidato> candidatos, Map<String, Integer> votosPorEscolha) {
        this.cargo = cargo;
        this.candidatos = Collections.unmodifiableList(new ArrayList<>(candidatos));
        this.votosPorEscolha = new HashMap<>(votosPorEscolha);
    }

    public Cargo getCargo() {
        return cargo;
    }

    public List<Candidato> getCandidatos() {
        return candidatos;
    }

    public int getVotosPara(String escolha) {
        return votosPorEscolha.getOrDefault(escolha, 0);
    }

    public int getTotalVotes() {
        int total = 0;
        for (int votos : votosPorEscolha.values()) {
            total += votos;
        }
        return total;
    }

    public double getPercentualPara(String escolha) {
        int total = getTotalVotes();
        if (total == 0) {
            return 0;
        }
        return getVotosPara(escolha) * 100.0 / total;
    }
}
