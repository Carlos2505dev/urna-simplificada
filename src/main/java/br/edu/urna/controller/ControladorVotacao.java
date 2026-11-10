/*
 * Arquivo: VotingController.java
 * Fluxo de votação: trata as teclas da urna e atualiza a tela.
 */
package br.edu.urna.controller;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.model.StatusEleicao;
import br.edu.urna.service.ServicoVotacao;
import br.edu.urna.util.ConstantesVotacao;
import br.edu.urna.view.TelaVotacao;
import java.util.Optional;
import javax.swing.Timer;

/**
 * Recebe os eventos do teclado, valida cada etapa e pede a exibição na tela.
 */
public class ControladorVotacao {

    private final ServicoVotacao servicoVotacao;
    private TelaVotacao tela;
    private String numeroDigitado = "";
    private Candidato candidatoSelecionado;
    private boolean brancoSelecionado;
    private boolean numeroInvalido;
    private boolean senhaDigitada;
    private boolean aguardandoProximoEleitor;
    private int indiceCargo;

    public ControladorVotacao(ServicoVotacao servicoVotacao) {
        this.servicoVotacao = servicoVotacao;
    }

    public void definirTela(TelaVotacao tela) {
        this.tela = tela;
        tela.mostrarCargo(cargoAtual());
        tela.mostrarMensagem("Aguardando senha do mesário para iniciar a votação.");
    }

    public void teclaDigitada(String digito) {
        if (aguardandoProximoEleitor) {
            return;
        }
        if (estaEncerrada()) {
            tela.mostrarMensagem("Votação encerrada.");
            return;
        }
        if (numeroDigitado.length() >= tamanhoNumeroAtual()) {
            return;
        }
        brancoSelecionado = false;
        numeroDigitado += digito;
        tela.mostrarNumeroDigitado(numeroDigitado);
        if (numeroDigitado.length() < tamanhoNumeroAtual()) {
            return;
        }
        processarNumeroCompleto();
    }

    public void teclaCorrigir() {
        if (aguardandoProximoEleitor) {
            return;
        }
        if (estaEncerrada()) {
            tela.mostrarMensagem("Votação encerrada.");
            return;
        }
        limparSelecao();
        tela.mostrarMensagem("Digite novamente o número do candidato.");
    }

    public void teclaBranco() {
        if (aguardandoProximoEleitor) {
            return;
        }
        if (!estaAberta()) {
            tela.mostrarMensagem("Aguarde a senha do mesário para iniciar a votação.");
            return;
        }
        limparSelecao();
        brancoSelecionado = true;
        tela.mostrarVotoBranco();
        tela.mostrarMensagem("Voto em branco selecionado. Aperte CONFIRMA para concluir.");
    }

    public void teclaConfirmar() {
        if (aguardandoProximoEleitor) {
            return;
        }
        StatusEleicao status = servicoVotacao.obterStatus();
        if (status == StatusEleicao.ENCERRADA) {
            tela.mostrarMensagem("Votação encerrada.");
            return;
        }
        if (senhaDigitada) {
            alternarEleicao(status);
            return;
        }
        if (status != StatusEleicao.ABERTA) {
            tela.mostrarMensagem("Aguarde a senha do mesário para iniciar a votação.");
            return;
        }
        if (candidatoSelecionado == null && !brancoSelecionado && !numeroInvalido) {
            tela.mostrarMensagem("Digite o número do candidato ou aperte BRANCO.");
            return;
        }
        servicoVotacao.registrarVoto(cargoAtual(), resolverEscolha());
        avancarCargo();
    }

    public boolean eConfirmacaoDoUltimoVoto() {
        return !aguardandoProximoEleitor
                && servicoVotacao.obterStatus() == StatusEleicao.ABERTA
                && indiceCargo == Cargo.values().length - 1
                && (candidatoSelecionado != null || brancoSelecionado || numeroInvalido);
    }

    private void processarNumeroCompleto() {
        if (ConstantesVotacao.SENHA.equals(numeroDigitado) && indiceCargo == 0) {
            senhaDigitada = true;
            String acao = servicoVotacao.obterStatus() == StatusEleicao.NAO_INICIADA ? "iniciar" : "encerrar";
            tela.mostrarMensagem("Senha correta. Confirme para " + acao + " a votação.");
            return;
        }
        if (!estaAberta()) {
            limparSelecao();
            tela.mostrarMensagem("A votação ainda não foi iniciada pelo mesário.");
            return;
        }
        Optional<Candidato> candidato = servicoVotacao.buscarCandidato(cargoAtual(), numeroDigitado);
        if (candidato.isPresent()) {
            candidatoSelecionado = candidato.get();
            tela.mostrarCandidato(candidatoSelecionado);
                tela.mostrarMensagem("CONFIRMA para CONFIRMAR este voto\nCORRIGE para REINICIAR este voto");
        } else {
            numeroInvalido = true;
            tela.mostrarNumeroInvalido(numeroDigitado);
            tela.mostrarMensagem("Número errado. O voto será nulo se confirmado. Use CORRIGE para corrigir.");
        }
    }

    private void alternarEleicao(StatusEleicao status) {
        limparSelecao();
        if (status == StatusEleicao.NAO_INICIADA) {
            servicoVotacao.iniciarEleicao();
            tela.mostrarRelatorios(servicoVotacao.gerarRelatorios(), "Relatório inicial");
            tela.mostrarMensagem("Votação iniciada. Digite o número do candidato.");
        } else {
            servicoVotacao.encerrarEleicao();
            tela.mostrarRelatorios(servicoVotacao.gerarRelatorios(), "Relatório final da eleição");
            tela.mostrarMensagem("Votação encerrada.");
        }
        tela.mostrarCargo(cargoAtual());
    }

    private void avancarCargo() {
        limparSelecao();
        indiceCargo++;
        if (indiceCargo == Cargo.values().length) {
            indiceCargo = 0;
            aguardandoProximoEleitor = true;
            tela.mostrarFim();
            tela.mostrarMensagem("Aguarde para iniciar a votação do próximo eleitor.");
            tela.definirTeclasAtivas(false);

            Timer nextVoterTimer = new Timer(3000, event -> {
                tela.limparTela();
                tela.mostrarCargo(cargoAtual());
                tela.mostrarMensagem("Voto concluído. Próximo eleitor.");
                aguardandoProximoEleitor = false;
                tela.definirTeclasAtivas(true);
            });
            nextVoterTimer.setRepeats(false);
            nextVoterTimer.start();
        } else {
            tela.mostrarCargo(cargoAtual());
            tela.mostrarMensagem("Agora vote para " + cargoAtual().getDescricao() + ".");
        }
    }

    private String resolverEscolha() {
        if (brancoSelecionado) {
            return ConstantesVotacao.ESCOLHA_BRANCO;
        }
        if (candidatoSelecionado != null) {
            return candidatoSelecionado.getNumero();
        }
        return ConstantesVotacao.ESCOLHA_NULO;
    }

    private void limparSelecao() {
        numeroDigitado = "";
        candidatoSelecionado = null;
        brancoSelecionado = false;
        numeroInvalido = false;
        senhaDigitada = false;
        tela.limparTela();
    }

    private Cargo cargoAtual() {
        return Cargo.values()[indiceCargo];
    }

    private int tamanhoNumeroAtual() {
        return cargoAtual() == Cargo.VEREADOR ? 5 : 2;
    }

    private boolean estaAberta() {
        return servicoVotacao.obterStatus() == StatusEleicao.ABERTA;
    }

    private boolean estaEncerrada() {
        return servicoVotacao.obterStatus() == StatusEleicao.ENCERRADA;
    }
}
