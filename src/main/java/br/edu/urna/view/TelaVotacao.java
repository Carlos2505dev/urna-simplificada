/*
 * Arquivo: TelaVotacao.java
 * Contrato da tela da urna, usado pelo controlador.
 */
package br.edu.urna.view;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.model.RelatorioEleitoral;
import java.util.List;

/**
 * Operações de exibição que o controlador pode solicitar.
 */
public interface TelaVotacao {

    void mostrarCargo(Cargo cargo);

    void mostrarNumeroDigitado(String numero);

    void mostrarCandidato(Candidato candidato);

    void mostrarNumeroInvalido(String numero);

    void mostrarVotoBranco();

    void mostrarVotoComputado();

    void mostrarFim();

    void definirTeclasAtivas(boolean ativas);

    void limparTela();

    void mostrarMensagem(String mensagem);

    void mostrarRelatorios(List<RelatorioEleitoral> relatorios, String titulo);
}
