/*
 * Repositório local de candidatos padrão.
 */
package br.edu.urna.repository.local;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.model.CatalogoDeCandidatos;
import br.edu.urna.repository.RepositorioCandidatos;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Mantém os candidatos do catálogo local em memória.
 */
public class RepositorioLocalCandidatos implements RepositorioCandidatos {

    private final List<Candidato> candidatos = new ArrayList<>(CatalogoDeCandidatos.criarCandidatosPadrao());

    @Override
    public List<Candidato> buscarPorCargo(Cargo cargo) {
        return candidatos.stream()
                .filter(candidato -> candidato.getCargo() == cargo)
                .toList();
    }

    @Override
    public Optional<Candidato> buscarPorCargoENumero(Cargo cargo, String numero) {
        return candidatos.stream()
                .filter(candidato -> candidato.getCargo() == cargo)
                .filter(candidato -> candidato.getNumero().equals(numero))
                .findFirst();
    }

    @Override
    public boolean estaVazio() {
        return candidatos.isEmpty();
    }

    @Override
    public void salvarTodos(List<Candidato> candidatos) {
        this.candidatos.clear();
        this.candidatos.addAll(candidatos);
    }
}