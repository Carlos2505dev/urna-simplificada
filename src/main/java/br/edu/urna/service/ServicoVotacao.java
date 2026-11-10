/*
 * Arquivo: VotingService.java
 * Regras de negócio da eleição.
 */
package br.edu.urna.service;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.model.CatalogoDeCandidatos;
import br.edu.urna.model.RelatorioEleitoral;
import br.edu.urna.model.StatusEleicao;
import br.edu.urna.repository.RepositorioCandidatos;
import br.edu.urna.repository.RepositorioVotos;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controla o ciclo da votação e a contagem dos votos.
 * Cada votação recebe um identificador novo, assim o relatório inicial começa zerado.
 */
public class ServicoVotacao {

    private final RepositorioCandidatos repositorioCandidatos;
    private final RepositorioVotos repositorioVotos;
    private StatusEleicao status = StatusEleicao.NAO_INICIADA;
    private String identificadorEleicao;

    public ServicoVotacao(RepositorioCandidatos repositorioCandidatos, RepositorioVotos repositorioVotos) {
        this.repositorioCandidatos = repositorioCandidatos;
        this.repositorioVotos = repositorioVotos;
    }

    public void prepararCandidatos() {
        repositorioCandidatos.salvarTodos(CatalogoDeCandidatos.criarCandidatosPadrao());
    }

    public void iniciarEleicao() {
        if (status != StatusEleicao.NAO_INICIADA) {
            throw new IllegalStateException("A votação já foi iniciada ou encerrada.");
        }
        identificadorEleicao = UUID.randomUUID().toString();
        status = StatusEleicao.ABERTA;
    }

    public void encerrarEleicao() {
        if (status != StatusEleicao.ABERTA) {
            throw new IllegalStateException("Não é possível encerrar uma votação que não foi iniciada.");
        }
        status = StatusEleicao.ENCERRADA;
    }

    public StatusEleicao obterStatus() {
        return status;
    }

    public Optional<Candidato> buscarCandidato(Cargo cargo, String numero) {
        exigirEleicaoAberta();
        return repositorioCandidatos.buscarPorCargoENumero(cargo, numero);
    }

    public void registrarVoto(Cargo cargo, String escolha) {
        exigirEleicaoAberta();
        repositorioVotos.registrarVoto(identificadorEleicao, cargo, escolha);
    }

    public List<RelatorioEleitoral> gerarRelatorios() {
        if (status == StatusEleicao.NAO_INICIADA) {
            throw new IllegalStateException("A votação ainda não foi iniciada.");
        }
        List<RelatorioEleitoral> relatorios = new ArrayList<>();
        for (Cargo cargo : Cargo.values()) {
            relatorios.add(new RelatorioEleitoral(
                    cargo,
                    repositorioCandidatos.buscarPorCargo(cargo),
                    repositorioVotos.contarVotos(identificadorEleicao, cargo)));
        }
        return relatorios;
    }

    private void exigirEleicaoAberta() {
        if (status != StatusEleicao.ABERTA) {
            throw new IllegalStateException("A votação não está aberta.");
        }
    }
}
