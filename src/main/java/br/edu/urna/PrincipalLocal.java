/*
 * Ponto de entrada da urna em modo local, sem dependências do Firebase.
 */
package br.edu.urna;

import br.edu.urna.controller.ControladorVotacao;
import br.edu.urna.repository.local.RepositorioLocalCandidatos;
import br.edu.urna.repository.local.RepositorioLocalVotos;
import br.edu.urna.service.ServicoVotacao;
import br.edu.urna.view.JanelaVotacao;
import javax.swing.SwingUtilities;

/**
 * Inicia a aplicação usando persistência local.
 */
public class PrincipalLocal {

    private PrincipalLocal() {
    }

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(() -> {
                ServicoVotacao servicoVotacao = new ServicoVotacao(
                    new RepositorioLocalCandidatos(), new RepositorioLocalVotos());
                servicoVotacao.prepararCandidatos();

            ControladorVotacao controlador = new ControladorVotacao(servicoVotacao);
            JanelaVotacao janela = new JanelaVotacao(controlador);
            controlador.definirTela(janela);
            janela.setVisible(true);
        });
    }
}