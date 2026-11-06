/*
 * Arquivo: ReportDialog.java
 * Janela com o relatório da eleição, separado por cargo.
 */
package br.edu.urna.view;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.ElectionReport;
import br.edu.urna.util.VotingConstants;
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
public final class ReportDialog {

    private static final int TEXT_ROWS = 20;
    private static final int TEXT_COLUMNS = 46;

    private ReportDialog() {
    }

    public static void show(Component parent, String title, List<ElectionReport> reports) {
        JTextArea textArea = new JTextArea(buildText(reports), TEXT_ROWS, TEXT_COLUMNS);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JOptionPane.showMessageDialog(parent, new JScrollPane(textArea), title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String buildText(List<ElectionReport> reports) {
        StringBuilder text = new StringBuilder();
        for (ElectionReport report : reports) {
            text.append(report.getOffice().getDescription().toUpperCase()).append(System.lineSeparator());
            for (Candidate candidate : report.getCandidates()) {
                String label = candidate.getName() + " (" + candidate.getNumber() + ")";
                if (candidate.getViceName() != null) {
                    label += " - Vice: " + candidate.getViceName();
                }
                appendLine(text, label, report, candidate.getNumber());
            }
            appendLine(text, "Votos em branco", report, VotingConstants.BLANK_CHOICE);
            appendLine(text, "Votos nulos", report, VotingConstants.NULL_CHOICE);
            text.append(System.lineSeparator());
        }
        return text.toString();
    }

    private static void appendLine(StringBuilder text, String label, ElectionReport report, String choice) {
        text.append(String.format(Locale.ROOT, "%s: %.0f%% (%d votos)%n",
                label, report.getPercentageFor(choice), report.getVotesFor(choice)));
    }
}
