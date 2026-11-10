/*
 * Arquivo: ReportDialog.java
 * Janela com o relatório da eleição, separado por cargo.
 */
package br.edu.urna.view;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.RelatorioEleitoral;
import br.edu.urna.util.ConstantesVotacao;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Monta e exibe o texto do relatório.
 */
public final class DialogoRelatorio {

    private static final int TEXT_ROWS = 20;
    private static final int TEXT_COLUMNS = 46;

        private DialogoRelatorio() { 
    }

    public static void mostrar(Component pai, String titulo, List<RelatorioEleitoral> relatorios) {
        JTextArea areaTexto = new JTextArea(montarTexto(relatorios), TEXT_ROWS, TEXT_COLUMNS);
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            JOptionPane.showMessageDialog(pai, new JScrollPane(areaTexto), titulo, 
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String montarTexto(List<RelatorioEleitoral> relatorios) {
        StringBuilder text = new StringBuilder();
        for (RelatorioEleitoral relatorio : relatorios) {
            text.append(relatorio.getCargo().getDescricao().toUpperCase()).append(System.lineSeparator());
            for (Candidato candidato : relatorio.getCandidatos()) {
                String label = candidato.getNome() + " (" + candidato.getNumero() + ")";
                if (candidato.getNomeVice() != null) {
                    label += " - Vice: " + candidato.getNomeVice();
                }
                adicionarLinha(text, label, relatorio, candidato.getNumero());
            }
            adicionarLinha(text, "Votos em branco", relatorio, ConstantesVotacao.ESCOLHA_BRANCO);
            adicionarLinha(text, "Votos nulos", relatorio, ConstantesVotacao.ESCOLHA_NULO);
            text.append(System.lineSeparator());
        }
        return text.toString();
    }

    private static void adicionarLinha(StringBuilder text, String label, RelatorioEleitoral relatorio, String escolha) {
        text.append(String.format(Locale.ROOT, "%s: %.0f%% (%d votos)%n",
                label, relatorio.getPercentualPara(escolha), relatorio.getVotosPara(escolha)));
    }
}
